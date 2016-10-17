package com.javacoders.websocketizer;

/**
 * Represents a request handler for a method uniquely identified by a URL.
 * 
 * @author shivam.maharshi
 */
public class RequestHandler {

	private String method;

	public RequestHandler(String method) {
		super();
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

}
