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
  private String returnType;
  private String packge;
  private String filepath;
  private List<InputParam> inputs;
  private RequestHandler requestHandler;
  private RequestContext requestContext;

  public ServiceBlueprint(String url, String returnType, String name, List<InputParam> inputs, RequestHandler requestHandler,
      RequestContext requestContext) {
    super();
    this.url = url;
    this.returnType = returnType;
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
  
  public String getReturnType() {
    return returnType;
  }

  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }
  
  public String getPackge() {
    return packge;
  }

  public void setPackge(String packge) {
    this.packge = packge;
  }
  
  public String getFilepath() {
    return filepath;
  }

  public void setFilepath(String filepath) {
    this.filepath = filepath;
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
