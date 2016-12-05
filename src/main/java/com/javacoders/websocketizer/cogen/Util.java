package com.javacoders.websocketizer.cogen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

/**
 * Hold the utils methods.
 * 
 * @author shivam.maharshi
 */
public class Util {

  public static <T> List<Object> parseMessage(String message, Class T) {
    WSRequest<T> request = new Gson().fromJson(message, WSRequest.class);
    List<Object> params = new ArrayList<Object>();
    HashMap<String, String> path = request.getPath();
    for (String key : path.keySet())
      params.add(path.get(key));
    HashMap<String, String> query = request.getQuery();
    for (String key : query.keySet())
      params.add(query.get(key));
    HashMap<String, String> matrix = request.getMatrix();
    for (String key : matrix.keySet())
      params.add(matrix.get(key));
    if (T != Void.class) {
      params.add(request.getBody());
    }
    return params;
  }

  public static String getJson(Object obj) {
    return new Gson().toJson(obj);
  }

}
