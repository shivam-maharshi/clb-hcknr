package com.javacoders.websocketizer;

import java.lang.reflect.Method;

/**
 * Represents a request handler for a method uniquely identified by a URL.
 * 
 * @author shivam.maharshi
 */
public class RequestHandler {

	private Method method;

	public RequestHandler(Method method) {
		super();
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

}
