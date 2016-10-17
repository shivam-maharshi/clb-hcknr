package com.javacoders.websocketizer;

/**
 * Represents context of a request stored in the class state.
 * 
 * @author shivam.maharshi
 */
public class RequestContext {

	private Class instance;

	public RequestContext(Class instance) {
		super();
		this.instance = instance;
	}

	public Class getInstance() {
		return instance;
	}

	public void setInstance(Class instance) {
		this.instance = instance;
	}

}
