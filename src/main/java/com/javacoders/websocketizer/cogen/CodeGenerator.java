package com.javacoders.websocketizer.cogen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;

import com.javacoders.websocketizer.InputParam;
import com.javacoders.websocketizer.ParamType;
import com.javacoders.websocketizer.ServiceBlueprint;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeSpec;


/**
 * Code Generator (a.k.a. CoGen) is responsible for taking any generic web
 * service represented by a {@link ServiceBlueprint} and automatically
 * generating a Web Socket service which exposes the same functionality as the
 * service represented by this {@link ServiceBlueprint}. The service blueprint
 * is generated by Pattern Identifier (a.k.a. PI).
 * 
 * @author shivam.maharshi
 */
public class CodeGenerator {
  
  public static final String WS_REQUEST = "import java.util.HashMap; public class WSRequest<T> { HashMap<String, String> path; HashMap<String, String> query; HashMap<String, String> matrix; T body; public HashMap<String, String> getPath() { return path; } public void setPath(HashMap<String, String> path) { this.path = path; } public HashMap<String, String> getQuery() { return query; } public void setQuery(HashMap<String, String> query) { this.query = query; } public HashMap<String, String> getMatrix() { return matrix; } public void setMatrix(HashMap<String, String> matrix) { this.matrix = matrix; } public T getBody() { return body; } public void setBody(T body) { this.body = body; } }";
  public static final String UTIL = "import java.util.ArrayList; import java.util.HashMap; import java.util.List;import com.google.gson.Gson;public class Util { public static <T> List<Object> parseMessage(String message, Class T) { WSRequest<T> request = new Gson().fromJson(message, WSRequest.class); List<Object> params = new ArrayList<Object>(); HashMap<String, String> path = request.getPath(); for (String key : path.keySet()) params.add(path.get(key)); HashMap<String, String> query = request.getQuery(); for (String key : query.keySet()) params.add(query.get(key)); HashMap<String, String> matrix = request.getMatrix(); for (String key : matrix.keySet()) params.add(matrix.get(key)); if (T != Void.class) { params.add(request.getBody()); } return params; } public static String getJson(Object obj) { return new Gson().toJson(obj); }}";

  /**
   * Takes a {@link ServiceBlueprint} created by PI after parsing the web
   * service to be automatically converted into Web Socket service.
   * 
   * @param sb
   * @throws IOException 
   */
  public static void generate(ServiceBlueprint sb, File dir) throws IOException {
    MethodSpec onOpen = constructOnOpenMethod();
    MethodSpec onMessage = constructOnMessageMethod(sb);
    MethodSpec onClose = constructOnCloseMethod();
    JavaFile file = JavaFile.
        builder(sb.getPackge() + ".autogenerated", constructWebSocketServerClass(sb, onOpen, onMessage, onClose))
        .build();
    file.writeTo(new File(sb.getFilepath() + "/autogenerated/"));
    createFrameworkFile(WS_REQUEST, "WSRequest.java", sb.getPackge(), sb.getFilepath());
    createFrameworkFile(UTIL, "Util.java", sb.getPackge(), sb.getFilepath());
  }
  
  private static void createFrameworkFile(String content, String name, String packge, String path) throws IOException {
    File file = new File(path + "/autogenerated/" + name);
    FileWriter writer = new FileWriter(file);
    writer.write("package " + packge +".autogenerated;");
    writer.write(content);
    writer.close();
  }
  
  private static TypeSpec constructWebSocketServerClass(ServiceBlueprint sb, MethodSpec onOpen, MethodSpec onMessage, MethodSpec onClose) {
    AnnotationSpec serverEndpointAnnotation = AnnotationSpec.builder(ServerEndpoint.class)
        .addMember("value", "$S", sb.getUrl())
        .build();
    
    String methodName = sb.getRequestHandler().getMethod();
    String methodType = sb.getRequestHandler().getMethodType().name();
    
     TypeSpec.Builder builder = TypeSpec.classBuilder(sb.getName() + "_" + methodName + "_" + methodType)
        .addAnnotation(serverEndpointAnnotation)
        .addModifiers(Modifier.PUBLIC);
     
     builder.addField(constructServiceBean(sb, builder))
        .addMethod(onOpen)
        .addMethod(onMessage)
        .addMethod(onClose);
        
        return builder.build();
  }
  
  private static FieldSpec constructServiceBean(ServiceBlueprint sb, TypeSpec.Builder builder) {
    return FieldSpec
        .builder(ClassName.bestGuess(sb.getRequestContext().getInstance()), "service", Modifier.PRIVATE)
        .addAnnotation(Autowired.class)
        .build();
  }
  
  private static MethodSpec constructOnOpenMethod() {
    return MethodSpec
        .methodBuilder("onOpen")
        .addAnnotation(OnOpen.class)
        .addException(IOException.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(void.class)
        .addParameter(Session.class, "session")
        .addStatement("$T.out.println($S)", System.class, "WebSocket Opened!")
        .build();
  }
  
  private static MethodSpec constructOnMessageMethod(ServiceBlueprint sb) {
     
     Builder methodBuilder = MethodSpec
         .methodBuilder("onMessage")
         .addAnnotation(OnMessage.class)
        .addException(IOException.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(void.class)
        .addException(InstantiationException.class)
        .addException(IllegalAccessException.class)
        .addParameter(String.class, "message")
        .addStatement("$T<Object> params = " + sb.getPackge() + ".autogenerated.Util.parseMessage(message, " + getRequestBodyClass(sb) + ".class)", List.class);
        
     addParametersDefinitionToBuilder(sb, methodBuilder);
     
     addMethodInvocationToBuilder(sb, methodBuilder);
        
     methodBuilder
        .addStatement("session.getBasicRemote().sendText("+ sb.getPackge() +".autogenerated.Util.getJson(output))")
        .addParameter(Session.class, "session");
        
     return methodBuilder.build();
  }
  
  private static String getRequestBodyClass (ServiceBlueprint sb) {
    String cls = "Void";
    List<InputParam> params = sb.getInputs();
    for (InputParam param : params) {
      if (param.getType().equals(ParamType.BODY)) {
        cls = param.getDataType();
        break;
      }
    }
    return cls;
  }
  
  public static void addParametersDefinitionToBuilder(ServiceBlueprint sb, Builder methodBuilder) {
      List<InputParam> inputs = sb.getInputs();
      for (int i=0; i<inputs.size(); i++) {
        methodBuilder.addStatement(inputs.get(i).getDataType() + " param" + i + " = ("+ inputs.get(i).getDataType() + ")params.get(" + i + ")");
      }
  }
  
  private static void addMethodInvocationToBuilder(ServiceBlueprint sb, Builder methodBuilder) {
    List<InputParam> inputs = sb.getInputs();
    StringBuilder params = new StringBuilder();
    
    if(inputs.size()>0)
      params.append("param0");
    
    for (int i=1; i<inputs.size(); i++) {
      params.append(",");
      params.append("param"+i);
    }
    
    String methodInvocation = new StringBuilder(sb.getReturnType())
        .append(" output = ")
        .append("service.")
        .append(sb.getRequestHandler().getMethod())
        .append("(")
        .append(params.toString())
        .append(")")
        .toString();
    
    methodBuilder.addStatement(methodInvocation);
  }
  
  private static MethodSpec constructOnCloseMethod() {
    return MethodSpec.methodBuilder("onClose")
        .addAnnotation(OnClose.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(void.class)
        .addException(IOException.class)
        .addParameter(CloseReason.class, "reason")
        .addStatement("session.close()")
        .addStatement("$T.out.println($S)", System.class, "WebSocket Closed!")
        .addParameter(Session.class, "session")
        .build();
  }
  
}
