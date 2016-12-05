package com.javacoders.websocketizer;

/**
 * Represents the type of parameter in a URL.
 * 
 * @author shivam.maharshi
 */
public enum ParamType {
  
  QUERY("QueryParam"), SEGMENT("PathParam"), MATRIX("MatrixParam");
  
  String value;
  
  ParamType(String value) {
    this.value = value;
  }
  
  public static ParamType getEnum(String value) {
    for(ParamType pt : ParamType.values()) {
      if(pt.value.equalsIgnoreCase(value)) {
        return pt;
      }
    }
    return null;
  }

}
