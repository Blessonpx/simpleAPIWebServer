package simpleWebServer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

/*
 * 
 * Decode HTTP Request
 * Route request to the correct RequestRunner
 * Write Response to output stream
 * 
 * 
 * */
public class HttpHandler {
	private final Map<String,RequestRunner> routes;
	
	public HttpHandler(final Map<String,RequestRunner> routes) {
		this.routes=routes;
	}
	
	/*
	 * 
	 * We decorate our OutputStream to a BufferedWriter which let’s us write text to a character-output stream.
	 * 
	 * */
	
	public void handleConnection(final InputStream inputStream,OutputStream outputStream) throws IOException{
		final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
		Optional<HttpRequest> request = HttpDecoder.decode(inputStream);
		
		request.ifPresentOrElse((r)->handleRequest(r,bufferedWriter),()->handleInvalidRequest(bufferedWriter));
		
		bufferedWriter.close();
		inputStream.close();
	}
	
	/*
	 * 
	 * Steps:
	 * Decode message into a HttpRequest object
	 * If present, handle
	 * Else, handle invalid request
	 * Close output and input streams
	 * Invalid Request scenario:
	 * Build a HTTP Response object with status code 400 and a message of “Bad Request”.
	 * 
	 * */
	
	
	private void handleInvalidRequest(final BufferedWriter bufferedWriter) {
		HttpResponse notFoundResponse = new HttpResponse.Builder().setStatusCode(400).setEntity("Bad Response ..").build();
		ResponseWriter.writeResponse(bufferedWriter,notFoundResponse);	
	}
	
	/*
	 * Valid Request scenario:
	 * Get the route key from HttpRequest object ( uri path ), if present we extract the RequestRunner from the Map of routes, execute and write the response.
	 * Otherwise we create a not found Response with a status code of 404. 
	 * 
	 * */
	
	private void handleRequest(final HttpRequest request,final BufferedWriter bufferedWriter) {
		final String routeKey= request.getHttpMethod().name().concat(request.getUri().getRawPath());
		
		if(routes.containsKey(routeKey)) {
			ResponseWriter.writeResponse(bufferedWriter,routes.get(routeKey).run(request));
		}else {
			//Not Found
			ResponseWriter.writeResponse(bufferedWriter,new HttpResponse.Builder().setStatusCode(404).setEntity("Route Not Found ..").build());
		}
	}
	
	
}
