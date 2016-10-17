package com.javacoders.websocketizer;

import java.util.Collection;

/**
 * Represents a method uniquely identified by a URL.
 * 
 * @author shivam.maharshi
 */
public class ServiceBlueprint {

	private String url;
	private Collection<InputParam> inputs;
	private RequestHandler requestHandler;
	private RequestContext requestContext;

	public ServiceBlueprint(String url, Collection<InputParam> inputs, RequestHandler requestHandler,
			RequestContext requestContext) {
		super();
		this.url = url;
		this.inputs = inputs;
		this.requestHandler = requestHandler;
		this.requestContext = requestContext;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Collection<InputParam> getInputs() {
		return inputs;
	}

	public void setInputs(Collection<InputParam> inputs) {
		this.inputs = inputs;
	}

	public RequestHandler getRequestHandler() {
		return requestHandler;
	}

	public void setRequestHandler(RequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}

	public RequestContext getRequestContext() {
		return requestContext;
	}

	public void setRequestContext(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

}
