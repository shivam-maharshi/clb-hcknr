package com.javacoders.websocketizer;

import java.util.Collection;

/**
 * Represents a method uniquely identified by a URL. 
 * 
 * @author shivam.maharshi
 */
public class ServiceBlueprint {
  
  String url;
  Collection<InputParam> inputs;
  RequestHandler requestHandler;
  RequestContext requestContext;
  
}
