package simpleWebServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {
	private final Map<String,RequestRunner> routes;
	private final ServerSocket socket;
	private final Executor threadpool;
	private HttpHandler handler;
	
	public enum HttpMethod{
		GET,
		PUT,
		POST,
		PATCH
	}
	
	public Server(int port) throws IOException{
		routes=new HashMap<>();
		threadpool = Executors.newFixedThreadPool(100);
		socket = new ServerSocket(port);
	}
	
	public void addRoute(HttpMethod opCode,String route,RequestRunner runner) {
		routes.put(opCode.name().concat(route),runner);
	}
	
	/*
	 * 
	 * We’ll now add a start method which will wait for incoming requests and handle them:
	 * socket.accept() is blocking so handleConnection will only be called when a client connects to the port defined.
	 * 
	 * */
	public void start() throws IOException{
		handler = new HttpHandler(routes);
		while(true) {
			Socket clientConnection = socket.accept();
			handleConnection(clientConnection);
		}
	}
	/*
	 * We’ll use the HttpHandler Object created to handle the connection.
	 * However, we don’t want this 1 request to block all other requests from executing, so we will wrap this functionality into a Runnable.
	 * The request / response lifecycle of each request will therefore be handled by 1 thread ( a synchronous server ).
	 * If we wanted to be able to handle 1 request across multiple threads then we would need to use the Java NIO lower level library ( which stands for new IO ).
	 * 
	 * */

	// This becomes the next code segment
//	private void handleConnection(Socket clientConnection) {
//		try {
//			handler.handleConnection(clientConnection.getInputStream(),clientConnection.getOutputStream());
//		}catch(IOException Ignored) {
//			
//		}
//	}
	/*
	 * Capture each Request / Response lifecycle in a thread
	 * executed on the threadPool.
	 */
	
	
	private void handleConnection(Socket clientConnection) {
		Runnable httpRequestRunner=()->{
			try {
				handler.handleConnection(clientConnection.getInputStream(),clientConnection.getOutputStream());
			}catch (IOException Ignored){
				
			}
		};
		threadpool.execute(httpRequestRunner);
	}
	
	
}
