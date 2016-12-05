package com.javacoders.websocketizer.cogen;

import java.io.File;
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
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.javacoders.websocketizer.InputParam;
import com.javacoders.websocketizer.ParamType;
import com.javacoders.websocketizer.ServiceBlueprint;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;
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

  /**
   * Takes a {@link ServiceBlueprint} created by PI after parsing the web
   * service to be automatically converted into Web Socket service.
   * 
   * @param sb
   * @throws IOException 
   */
  public static void generate(ServiceBlueprint sb) throws IOException {
    
    MethodSpec onOpen = constructOnOpenMethod();
    MethodSpec onMessage = constructOnMessageMethod(sb);
    MethodSpec onClose = constructOnCloseMethod();
    JavaFile file = JavaFile.
        builder("com.javacoders.websocketizer.autogenerated", constructWebSocketServerClass(sb, onOpen, onMessage, onClose))
        .build();
    createJavaFile(file, "/src/main/java");
  }
  
  private static void createJavaFile(JavaFile file, String path) throws IOException {
    file.writeTo(new File(System.getProperty("user.dir") + path));
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
        .addStatement("session.getBasicRemote().sendText(Util.getJson(output))")
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
    
    String methodInvocation = new StringBuilder(sb.getRetutnType())
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
        .addParameter(CloseReason.class, "reason")
        .addStatement("session.close()")
        .addStatement("$T.out.println($S)", System.class, "WebSocket Closed!")
        .addParameter(Session.class, "session")
        .build();
  }
  
}
