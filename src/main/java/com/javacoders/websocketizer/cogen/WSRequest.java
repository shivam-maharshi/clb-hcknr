package com.javacoders.websocketizer.cogen;

import java.util.HashMap;

/**
 * Standard request object for communication of RESTFul applications with a Web
 * Socket server. Helps in the standardization of the request de-serialization.
 * 
 * @author shivam.maharshi
 */
public class WSRequest<T> {

  HashMap<String, String> path;
  HashMap<String, String> query;
  HashMap<String, String> matrix;
  T body;
  
}
