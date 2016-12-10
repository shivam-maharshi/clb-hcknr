package com.javacoders.websocketizer.cogen.ws;

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

  public HashMap<String, String> getPath() {
    return path;
  }

  public void setPath(HashMap<String, String> path) {
    this.path = path;
  }

  public HashMap<String, String> getQuery() {
    return query;
  }

  public void setQuery(HashMap<String, String> query) {
    this.query = query;
  }

  public HashMap<String, String> getMatrix() {
    return matrix;
  }

  public void setMatrix(HashMap<String, String> matrix) {
    this.matrix = matrix;
  }

  public T getBody() {
    return body;
  }

  public void setBody(T body) {
    this.body = body;
  }

}
