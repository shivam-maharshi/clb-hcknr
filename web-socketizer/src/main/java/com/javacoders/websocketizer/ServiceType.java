package com.javacoders.websocketizer;

/**
 * Represent different type of web services.
 * 
 * @author shivam.maharshi
 */
public enum ServiceType {
  
  REST("rest"), WS("ws");
  
  String value;
  
  ServiceType(String value) {
    this.value = value;
  }
  
  public static ServiceType getEnum(String value) {
    for(ServiceType st : ServiceType.values()) {
      if(st.value.equalsIgnoreCase(value)) {
        return st;
      }
    }
    return null;
  }

}
