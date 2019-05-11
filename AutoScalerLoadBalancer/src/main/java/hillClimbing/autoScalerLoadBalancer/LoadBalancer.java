package hillClimbing.autoScalerLoadBalancer;

import com.amazonaws.util.IOUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import hillClimbing.database.ClimbRequestCostEntry;
import hillClimbing.solver.SolverArgumentParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class LoadBalancer implements Runnable {

    private static final int LOAD_BALANCER_PORT = 80;
    private static final int INSTANCE_PORT = 8000;
    private static final Map<String, ClimbRequestCostEntry> requestsCache = new HashMap<>();

    public LoadBalancer() {
        new Thread(this).start();
    }

    public void run() {
        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress(LOAD_BALANCER_PORT), 0);
            server.createContext("/climb", new Climb());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
        } catch (IOException ioe) {
            printErr("Error while creating the server: " + ioe.getMessage());
        }
    }

    static class Climb implements HttpHandler {

        public void handle(HttpExchange t) {
            try {
                URI uri = t.getRequestURI();
                ClimbRequestCostEntry request = queryToClimbRequestCostEntry(uri.getQuery());
                long cost = getCostFromRequest(request);
                Instance instance = InstanceManager.getLeastUsedInstance();
                instance.newRequest(cost);
                InputStream response;
                try {
                    response = forwardRequestToInstance(instance, uri, cost);
                } catch (IOException ioe) {
                    printErr("Error while communicating with instance [InstanceID = " + instance.getInstanceID() + "]: " + ioe.getMessage());
                    instance.requestCompleted(cost);
                    return;
                }
                sendResponse(t, response);
                instance.requestCompleted(cost);
                log("Response sent to client [ClientIP = " + t.getRemoteAddress().toString() + "]");
            } catch (IOException ioe) {
                printErr("Error while sending response to client [ClientIP = " + t.getRemoteAddress().toString() + "]: " + ioe.getMessage());
            } catch (Exception e) {
                printErr("Error while parsing the arguments: " + e.getMessage());
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
        return 0;
    }

    private static InputStream forwardRequestToInstance(Instance instance, URI uri, long cost) throws IOException {
        URL url = new URL(String.format("%s:%d%s", instance.getInstanceIP(), INSTANCE_PORT, uri));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
        System.out.println("[Load Balancer - Thread " + Thread.currentThread().getId() + "] " + logMessage);
    }

    private static void printErr(String errorMessage) {
        System.err.println("[Load Balancer - Thread " + Thread.currentThread().getId() + "] " + errorMessage);
    }

}
