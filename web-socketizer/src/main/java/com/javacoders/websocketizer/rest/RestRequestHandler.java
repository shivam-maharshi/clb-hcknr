package com.javacoders.websocketizer.rest;

import com.javacoders.websocketizer.MethodType;
import com.javacoders.websocketizer.RequestHandler;

/**
 * Represents a request handler for a method uniquely identified by a URL.
 * 
 * @author shivam.maharshi
 */
public class RestRequestHandler implements RequestHandler {

  private String method;
  private MethodType methodType;

  public RestRequestHandler(String method, MethodType methodType) {
    super();
    this.method = method;
    this.methodType = methodType;
  }

  @Override
  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  @Override
  public MethodType getMethodType() {
    return methodType;
  }

  public void setMethodType(MethodType methodType) {
    this.methodType = methodType;
  }

}
