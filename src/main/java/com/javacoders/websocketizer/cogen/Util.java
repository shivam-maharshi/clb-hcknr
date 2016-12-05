package com.javacoders.websocketizer.cogen;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

/**
 * Hold the utils methods.
 * 
 * @author shivam.maharshi
 */
public class Util {
  
  public static List<Object> parseMessage(String message) {
    WSRequest request = new Gson().fromJson(message, WSRequest.class);
    List<Object> params = new ArrayList<Object>();
    return params;
  }
  
  public static String getJson(Object obj) {
    return new Gson().toJson(obj);
  }

}
