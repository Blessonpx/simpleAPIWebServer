package simpleWebServer;

import java.io.BufferedWriter;
import java.net.http.HttpResponse;

public class ResponseWriter {
	public static void writeResponse(final BufferedWriter outputStream,final HttpResponse response) {
		try {
			final int statusCode = response.getStatusCode();
			final String statusCodeMeaning = HttpStatusCode.STATUS_CODE.get(statusCode);
		}catch(Exception Ignored) {
			
		}
	}
}
