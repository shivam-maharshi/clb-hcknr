package com.javacoders.websocketizer;

/**
 * Represents HTTP type methods.
 *  
 * @author shivam.maharshi
 */
public enum MethodType {
  
  DELETE, GET, HEAD, POST, PUT;
  
  public static MethodType getEnum(String value) {
    for (MethodType mt : MethodType.values()) {
      if (mt.name().equalsIgnoreCase(value)) {
        return mt;
      }
    }
    return null;
  }
  
}
