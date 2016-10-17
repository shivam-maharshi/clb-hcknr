package com.javacoders.websocketizer.cogen;

import java.io.File;
import java.io.IOException;

import javax.lang.model.element.Modifier;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.javacoders.websocketizer.RequestContext;
import com.javacoders.websocketizer.RequestHandler;
import com.javacoders.websocketizer.ServiceBlueprint;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Code Generator (a.k.a. CoGen) is responsible for taking any generic web
 * service represented by a {@link ServiceBlueprint} and automatically
 * generating a Web Socket service which exposes the same functionality as the
 * service represented by this {@link ServiceBlueprint}. The service blueprint
 * is generated by PI.
 * 
 * @author shivam.maharshi
 */
public class CodeGenerator {

	/**
	 * Takes a {@link ServiceBlueprint} created by PI after parsing the web
	 * service to be automatically converted into Web Socket service.
	 * 
	 * @param sb
	 */
	public static void generate(ServiceBlueprint sb) {

		MethodSpec onOpen = MethodSpec.methodBuilder("onOpen").addAnnotation(OnOpen.class).addModifiers(Modifier.PUBLIC)
				.returns(void.class).addParameter(Session.class, "session")
				.addStatement("$T.out.println($S)", System.class, "WebSocket Opened!").build();

		String className = sb.getRequestContext().getInstance();
		String methodName = sb.getRequestHandler().getMethod();

		MethodSpec onMessage = MethodSpec.methodBuilder("onMessage").addAnnotation(OnMessage.class)
				.addException(IOException.class).addModifiers(Modifier.PUBLIC).returns(void.class)
				.addException(InstantiationException.class).addException(IllegalAccessException.class)
				.addParameter(String.class, "message")
				.addStatement("String output = " + className + ".class.newInstance()." + methodName + "()")
				.addStatement("session.getBasicRemote().sendText(output)").addParameter(Session.class, "session")
				.build();

		MethodSpec onClose = MethodSpec.methodBuilder("onClose").addAnnotation(OnClose.class)
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(CloseReason.class, "reason")
				.addStatement("$T.out.println($S)", System.class, "WebSocket Closed!")
				.addParameter(Session.class, "session").build();

		AnnotationSpec serverEndpointAnnotation = AnnotationSpec.builder(ServerEndpoint.class)
				.addMember("value", "$S", sb.getUrl()).build();

		TypeSpec autogenClass = TypeSpec.classBuilder("HelloWorld").addAnnotation(serverEndpointAnnotation)
				.addModifiers(Modifier.PUBLIC).addMethod(onOpen).addMethod(onMessage).addMethod(onClose).build();

		JavaFile javaFile = JavaFile.builder("com.javacoders.websocketizer.autogenerated", autogenClass).build();

		try {
			javaFile.writeTo(new File(System.getProperty("user.dir")+"/src/main/java"));
			javaFile.writeTo(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String url = "/hello";
		RequestHandler rh = new RequestHandler("hello");
		RequestContext rc = new RequestContext("com.javacoders.service.rs.RestService");
		ServiceBlueprint sb = new ServiceBlueprint(url, null, rh, rc);
		generate(sb);
	}

}
