package com.javacoders.websocketizer.client;

import java.io.File;
import java.util.Collection;

import com.javacoders.websocketizer.ServiceType;
import com.javacoders.websocketizer.cogen.ws.WSCodeGenerator;
import com.javacoders.websocketizer.parser.rest.RestServiceExtractor;
import com.javacoders.websocketizer.rest.RestServiceBlueprint;

/**
 * Takes input from command line and auto-generates code for the Web Service to
 * be converted into a Web Socket Service.
 * 
 * @author shivam.maharshi
 */
public class ConsoleClient {

  public static void main(String[] args) {
    String in = null;
    ServiceType from = null, to = null;
    for (String arg : args) {
      if (arg != null && !arg.isEmpty() && arg.startsWith("--input=")) {
        in = arg.split("=")[1];
      } else if (arg != null && !arg.isEmpty() && arg.startsWith("--from=")) {
        from = ServiceType.getEnum(arg.split("=")[1]);
      } else if (arg != null && !arg.isEmpty() && arg.startsWith("--to=")) {
        to = ServiceType.getEnum(arg.split("=")[1]);
      }
    }
    if (in != null && to != null && from != null) {
      File projectDir = new File(in);
      if (from == ServiceType.REST) {
        Collection<RestServiceBlueprint> blueprints = new RestServiceExtractor().extractBlueprints(projectDir);
        for (RestServiceBlueprint sb : blueprints)
          try {
            if (to == ServiceType.WS)
              new WSCodeGenerator().generate(sb, projectDir);
          } catch (RuntimeException e) {
            System.out.println(String.format("[SourceDir: %s, PackageName: %s, ClassName: %s, Exception: %s] ",
                sb.getSourceDir(), sb.getPackageName(), sb.getClassName(), e.getMessage()));
          }
      }
    } else {
      System.out.println("Please enter a valid inputs. Please see documentation for more details.");
    }
  }

}
