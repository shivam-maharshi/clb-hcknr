package com.javacoders.websocketizer;

/**
 * Represents the input param for a method identified by a URL.
 * 
 * @author shivam.maharshi
 */
public class InputParam {

	private String name;
	private ParamType type;

	public InputParam(String name, ParamType type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ParamType getType() {
		return type;
	}

	public void setType(ParamType type) {
		this.type = type;
	}

}
