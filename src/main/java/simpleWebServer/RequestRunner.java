package simpleWebServer;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/*
 * The RequestRunner is a Java interface which only has 1 method which takes a HttpResponse as an argument and returns a HttpRequest object.
 * This allows it to be a functional interface where we can supply this using a Lambda.
 * 
 * */

public interface RequestRunner {
	HttpResponse run(HttpRequest request);
}
