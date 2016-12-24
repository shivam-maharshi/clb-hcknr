package com.javacoders.websocketizer.rest;

import com.javacoders.websocketizer.RequestContext;

/**
 * Represents context of a request stored in the class state.
 * 
 * @author shivam.maharshi
 */
public class RestRequestContext implements RequestContext {

	private String instance;

	public RestRequestContext(String instance) {
		super();
		this.instance = instance;
	}

	@Override
	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

}
