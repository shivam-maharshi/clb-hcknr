package com.javacoders.websocketizer;

/**
 * Represents a request handler for a method uniquely identified by a URL.
 * 
 * @author shivam.maharshi
 */
public class RequestHandler {

  private String method;
  private MethodType methodType;

  public RequestHandler(String method, MethodType methodType) {
    super();
    this.method = method;
    this.methodType = methodType;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public MethodType getMethodType() {
    return methodType;
  }

  public void setMethodType(MethodType methodType) {
    this.methodType = methodType;
  }

}
