package com.javacoders.websocketizer;

/**
 * Represents context of a request stored in the class state.
 * 
 * @author shivam.maharshi
 */
public class RequestContext {

	private String instance;

	public RequestContext(String instance) {
		super();
		this.instance = instance;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

}
