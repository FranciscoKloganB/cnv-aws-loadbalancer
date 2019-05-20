package hillClimbing.autoScalerLoadBalancer;

import com.amazonaws.util.IOUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import hillClimbing.database.ClimbRequestCostEntry;
import hillClimbing.database.Database;
import hillClimbing.solver.SolverArgumentParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;

public class LoadBalancer implements Runnable {

    private final int IDLE_CONNECTION_TIMEOUT;
    private final int LOAD_BALANCER_PORT;
    private final int INSTANCE_PORT;
    private static final Map<String, ClimbRequestCostEntry> requestsCache = new HashMap<>();

    public LoadBalancer(Properties properties) {
        IDLE_CONNECTION_TIMEOUT = Integer.parseInt(properties.getProperty("loadBalancer.idleConnectionTimeout", "300000"));
        LOAD_BALANCER_PORT = Integer.parseInt(properties.getProperty("loadBalancer.port", "80"));
        INSTANCE_PORT = Integer.parseInt(properties.getProperty("instance.port", "8000"));
        new Thread(this).start();
    }

    public void run() {
        try {
            log(String.format("Creating server on port: %d", LOAD_BALANCER_PORT));
            final HttpServer server = HttpServer.create(new InetSocketAddress(LOAD_BALANCER_PORT), 0);
            server.createContext("/climb", new Climb());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            log("Server created");
        } catch (IOException ioe) {
            printErr(String.format("Error while creating the server: %s", ioe.getMessage()));
        }
    }

    class Climb implements HttpHandler {

        public void handle(HttpExchange t) {
            try {
                URI uri = t.getRequestURI();
                log(String.format("Received request [URI = %s] from client [ClientIP = %s]", uri, t.getRemoteAddress()));
                ClimbRequestCostEntry request = queryToClimbRequestCostEntry(uri.getQuery());
                log(String.format("Calculating cost of request [URI = %s]", uri));
                long cost = getCostFromRequest(request);
                log(String.format("Cost of request [URI = %s]: %d", uri, cost));
                log("Getting least used instance");
                Instance instance = InstanceManager.getLeastUsedInstance();
                log(String.format("Obtained instance [InstanceID = %s]", instance.getInstanceID()));
                instance.newRequest(cost);
                InputStream response;
                try {
                    log(String.format("Forwarding request [URI = %s] to instance [InstanceID = %s]", uri, instance.getInstanceID()));
                    response = forwardRequestToInstance(instance, uri);
                } catch (IOException ioe) {
                    printErr(String.format("Error while communicating with instance [InstanceID = %s]: %s", instance.getInstanceID() , ioe.getMessage()));
                    instance.requestCompleted(cost);
                    return;
                }

                instance.requestCompleted(cost);
                log(String.format("Forwarding response to request [URI = %s] to client [ClientIP = %s]", uri, t.getRemoteAddress()));
                sendResponse(t, response);
                log("Response forwarded to client [ClientIP = " + t.getRemoteAddress() + "]");
            } catch (IOException ioe) {
                printErr(String.format("Error while sending response to client [ClientIP = %s]: %s", t.getRemoteAddress() , ioe.getMessage()));
            } catch (Exception e) {
                printErr(String.format("Error while parsing the arguments: %s", e.getMessage()));
            }
        }
    }

    private static ClimbRequestCostEntry queryToClimbRequestCostEntry(String query) {
        final String[] params = query.split("&");
        final ArrayList<String> newArgs = new ArrayList<>();
        for (final String p : params) {
            final String[] splitParam = p.split("=");
            newArgs.add("-" + splitParam[0]);
            newArgs.add(splitParam[1]);
        }
        newArgs.add("-d");
        final String[] args = new String[newArgs.size()];
        int i = 0;
        for(String arg: newArgs) {
            args[i] = arg;
            i++;
        }
        return new ClimbRequestCostEntry(new SolverArgumentParser(args));
    }

    private static long getCostFromRequest(ClimbRequestCostEntry request) {
    	
    	ClimbRequestCostEntry entry;
    	
    	if (requestsCache.containsKey(request.getKey())) {
    		entry = requestsCache.get(request.getKey());
    	} else {
	    	List<ClimbRequestCostEntry> costEntries = Database.query(request.getKey());
	    	if (!costEntries.isEmpty()) {
	    		entry = costEntries.get(0);
	    		if (entry.getInstructions() != null) {
                    requestsCache.put(entry.getKey(), entry);
                }
	    	} else {
                double weightedCostSum = 0;
                double weightSum = 0;

	    	    List<ClimbRequestCostEntry> similarCostEntries = Database.scanCloseRequests(request);

	    	    for (ClimbRequestCostEntry climbRequestCostEntry : similarCostEntries) {
	    	        if (climbRequestCostEntry.getInstructions() != null) {
	    	            double weight = 1 / (1 + Math.sqrt(
	    	                    Math.pow(request.getxStartPoint() - climbRequestCostEntry.getxStartPoint(), 2) +
                                Math.pow(request.getyStartPoint() - climbRequestCostEntry.getyStartPoint(), 2)));

	    	            weightedCostSum += climbRequestCostEntry.getInstructions() * weight;
	    	            weightSum += weight;
                    }
                }

                return weightSum != 0 ? (long) (weightedCostSum / weightSum) : 0;
	    	}
    	}
    	
		return entry.getInstructions() != null ? entry.getInstructions() : 0;
    }

    private InputStream forwardRequestToInstance(Instance instance, URI uri) throws IOException {
        URL url = new URL(String.format("%s:%d%s", instance.getInstanceIP(), INSTANCE_PORT, uri));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(IDLE_CONNECTION_TIMEOUT);
        return connection.getInputStream();
    }

    private static void sendResponse(HttpExchange t, InputStream is) throws IOException {
        final OutputStream os = t.getResponseBody();
        try {
            final Headers headers = t.getResponseHeaders();
            headers.add("Content-Type", "image/png");
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Credentials", "true");
            headers.add("Access-Control-Allow-Methods", "POST, GET, HEAD, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
            t.sendResponseHeaders(200, 0);
            IOUtils.copy(is, os);
        } finally {
            is.close();
            os.close();
        }
    }

    private static void log(String logMessage) {
        System.out.println(String.format("[Load Balancer - Thread %d] %s", Thread.currentThread().getId(), logMessage));
    }

    private static void printErr(String errorMessage) {
        System.err.println(String.format("[Load Balancer - Thread %d] %s", Thread.currentThread().getId(), errorMessage));
    }

}
