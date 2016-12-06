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
  private String packge;
  private String srcDirPath;
  private String autogenPath;
  private Framework framework;
  private List<InputParam> inputs;
  private RequestHandler requestHandler;
  private RequestContext requestContext;

  public ServiceBlueprint(String url, String name, List<InputParam> inputs, RequestHandler requestHandler,
      RequestContext requestContext) {
    super();
    this.url = url;
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
  
  public String getPackge() {
    return packge;
  }

  public void setPackge(String packge) {
    this.packge = packge;
  }
  
  public Framework getFramework() {
    return framework;
  }

  public void setFramework(Framework framework) {
    this.framework = framework;
  }

  public String getSrcDirPath() {
    return srcDirPath;
  }

  public void setSrcDirPath(String srcDirPath) {
    this.srcDirPath = srcDirPath;
  }

  public String getAutogenPath() {
    return autogenPath;
  }

  public void setAutogenPath(String autogenPath) {
    this.autogenPath = autogenPath;
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
