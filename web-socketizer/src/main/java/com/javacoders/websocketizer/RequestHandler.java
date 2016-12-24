package com.javacoders.websocketizer;

/**
 * Every object representing a web service request handler must implement this
 * interface.
 * 
 * @author shivam.maharshi
 */
public interface RequestHandler {

  public String getMethod();

  public MethodType getMethodType();

}
