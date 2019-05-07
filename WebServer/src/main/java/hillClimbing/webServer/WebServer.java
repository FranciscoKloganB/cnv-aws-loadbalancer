package hillClimbing.webServer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import hillClimbing.database.ClimbRequestCostEntry;
import hillClimbing.instrumentation.ThreadMapper;
import hillClimbing.solver.Solver;
import hillClimbing.solver.SolverArgumentParser;
import hillClimbing.solver.SolverFactory;

import javax.imageio.ImageIO;

public class WebServer {

	public static void main(final String[] args) throws Exception {

		final HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

		server.createContext("/climb", new Climb());
		server.createContext("/ping", new Ping());

		server.setExecutor(Executors.newCachedThreadPool()); // Warning: infinite pool of threads
		server.start();

		System.out.println(server.getAddress().toString());
	}

	static class Climb implements HttpHandler {
		@Override
		public void handle(final HttpExchange t) throws IOException {

			final String query = t.getRequestURI().getQuery();
			System.out.println("> Query:\t" + query);
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
			SolverArgumentParser ap;
			try {
				ap = new SolverArgumentParser(args);
			}
			catch(Exception e) {
				System.out.println(e);
				return;
			}

			System.out.println("> Finished parsing args.");
			ThreadMapper.updateEntry(new ClimbRequestCostEntry(ap));

			final Solver s = SolverFactory.getInstance().makeSolver(ap);
			File responseFile = null;
			try {
				long before = System.currentTimeMillis();
				final BufferedImage outputImg = s.solveImage();
				long after = System.currentTimeMillis();
				ThreadMapper.TESTSendTime(after - before);
				//ThreadMapper.sendCountToDB();
				//ThreadMapper.TESTSendCountToDB();
				final String outPath = ap.getOutputDirectory();
				final String imageName = s.toString();
				if(ap.isDebugging()) {
					System.out.println("> Image name: " + imageName);
				}
				final Path imagePathPNG = Paths.get(outPath, imageName);
				ImageIO.write(outputImg, "png", imagePathPNG.toFile());
				responseFile = imagePathPNG.toFile();
			} catch (final IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}

			final Headers headers = t.getResponseHeaders();
			headers.add("Content-Type", "image/png");
			headers.add("Access-Control-Allow-Origin", "*");
			headers.add("Access-Control-Allow-Credentials", "true");
			headers.add("Access-Control-Allow-Methods", "POST, GET, HEAD, OPTIONS");
			headers.add("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
			sendResponse(t, Files.readAllBytes(responseFile.toPath()), responseFile.length());
		}
	}

	static class Ping implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			String response = "Pong";
			sendResponse(t, response.getBytes(), response.length());
		}
	}

	private static void sendResponse(HttpExchange t, byte[] data, long size) throws IOException {
		t.sendResponseHeaders(200, size);
		final OutputStream os = t.getResponseBody();
		os.write(data);
		os.close();
		System.out.println("> Sent response to " + t.getRemoteAddress().toString());
	}
}
