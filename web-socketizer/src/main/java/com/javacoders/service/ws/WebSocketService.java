package com.javacoders.service.ws;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Sample Web Socket service.
 * 
 * @author shivam.maharshi
 */
@ServerEndpoint("/hello")
public class WebSocketService {
	
	@OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("Message received: " + message);
        session.getBasicRemote().sendText("Hello!");
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        System.out.println("Closing a WebSocket due to: " + reason.getReasonPhrase());
    }

}
