package com.javacoders.websocketizer;

import java.util.List;

/**
 * Represents a method uniquely identified by a URL.
 * 
 * @author shivam.maharshi
 */
public class ServiceBlueprint {

  private String url;
  private String name;
  private String retutnType;
  private List<InputParam> inputs;
  private RequestHandler requestHandler;
  private RequestContext requestContext;

  public ServiceBlueprint(String url, String returnType, String name, List<InputParam> inputs, RequestHandler requestHandler,
      RequestContext requestContext) {
    super();
    this.url = url;
    this.retutnType = returnType;
    this.name = name;
    this.inputs = inputs;
    this.requestHandler = requestHandler;
    this.requestContext = requestContext;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
  
  public String getRetutnType() {
    return retutnType;
  }

  public void setRetutnType(String retutnType) {
    this.retutnType = retutnType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<InputParam> getInputs() {
    return inputs;
  }

  public void setInputs(List<InputParam> inputs) {
    this.inputs = inputs;
  }

  public RequestHandler getRequestHandler() {
    return requestHandler;
  }

  public void setRequestHandler(RequestHandler requestHandler) {
    this.requestHandler = requestHandler;
  }

  public RequestContext getRequestContext() {
    return requestContext;
  }

  public void setRequestContext(RequestContext requestContext) {
    this.requestContext = requestContext;
  }

}
