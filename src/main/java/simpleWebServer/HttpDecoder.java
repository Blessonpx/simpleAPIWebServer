package simpleWebServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale.Builder;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/*
 * 
 * The decode method breaks this into two steps:
 * Read all lines of text in the InputStream to a List<String>
 * Parse the List<String> into a HttpRequest Object ( if valid )
 * 
 * */
public class HttpDecoder {
	
	
	public enum HttpMethod {
	    GET,
	    PUT,
	    POST,
	    PATCH
	}
	
	public static Optional<HttpRequest> decode(final InputStream inputStream ){
		return readMessage(inputStream).flatMap(HttpDecoder::buildRequest);
	}
	
	
	/*
	 * 
	 * Reading the message:
	 * If no data is in the InputStream we return an empty Optional, otherwise the data is read into a char[] buffer created.
	 * The Scanner object is then used to read the buffer line by line and append each to an Array list that is returned.
	 * If any exception occurs, an empty Optional is returned.
	 * 
	 * 
	 * */
	
	private static Optional<List<String>> readMessage(final InputStream inputStream) throws IOException{
		try {
			if(!(inputStream.available()>0)) {
				return Optional.empty();
			}
			final char[] inBuffer =new char[inputStream.available()];
			final InputStreamReader inReader = new InputStreamReader(inputStream);
			final int read = inReader.read(inBuffer);
			
			List<String> message = new ArrayList<>();
			
			try(Scanner sc = new Scanner(new String(inBuffer))) {
				while(sc.hasNextLine()) {
					String line = sc.nextLine();
					message.add(line);
				}
				return Optional.of(message);
			}catch(Exception ignored){
				return Optional.empty();
			}
		}finally {
			
		}
		
	}
	
	public static Optional<HttpRequest> buildRequest(List<String> message){
		if (message.isEmpty()) {
			return Optional.empty();
		}
		String firstLine=message.get(0);
		String[] httpInfo = firstLine.split(" ");
		
		if(httpInfo.length!=3) {
			return Optional.empty();
		}
		String protocolVersion = httpInfo[2];
		if(protocolVersion.equals("HTTP/1.1")) {
			return Optional.empty();
		}
		try {
			HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
			requestBuilder.setHttpMethod(HttpMethod.valueOf(httpInfo[1]));
			requestBuilder.uri(new URI(httpInfo[1]));
			return Optional.of(addRequestHeaders(message,requestBuilder));
		}catch(URISyntaxException | IllegalArgumentException e) {
			return Optional.empty();
		}
	}
	
	private static HttpRequest addRequestHeaders(final List<String> message, final Builder builder) {
		final Map<String,List<String>> requestHeaders = new HashMap<>();
		
		if(message.size()>1) {
			for(int i=1;i<message.size();i++) {
				String header = message.get(i);
				int colonIndex = message.indexOf(":");
				if(!(colonIndex>0 && header.length()>colonIndex+1)) {
					break;
				}
				
				String headerName = header.substring(0,colonIndex);
				String headerValue = header.substring(colonIndex+1);
				
				requestHeaders.compute(headerName, (key,values)->{
					if(values != null) {
						values.add(headerValue);
					}else {
						values = new ArrayList<>();
					}
					return values;
				});
				
			}
		}
	}
	
	
	
	
}
