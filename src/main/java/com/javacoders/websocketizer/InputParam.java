package com.javacoders.websocketizer;

/**
 * Represents the input param for a method identified by a URL.
 * 
 * @author shivam.maharshi
 */
public class InputParam {

  // Used as the URL parameter identification.
  private String id;
  // Used as the method variable name identifier. 
	private String name;
	private ParamType type;

	public InputParam(String id, String name, ParamType type) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
	}
	
	public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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
