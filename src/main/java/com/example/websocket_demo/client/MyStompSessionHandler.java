package com.example.websocket_demo.client;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import com.example.websocket_demo.Message;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {
	
	private String username;
	
	public MyStompSessionHandler(String username) {
		this.username = username;
	}

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		  System.out.println("Client connected");
		  session.send("/app/connect", username);
		
		session.subscribe("/topic/messages", new StompFrameHandler() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				System.out.println("I got here");
				return Message.class;
			}
			
			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				try {
					if(payload instanceof Message) {
						Message message = (Message) payload;
						System.out.println("Received message: " + message.getUser() + ": " + message.getMessage());
					}
					
				}catch(Exception e) {
					System.out.println("Received unexpected payload type: " + payload.getClass());
				}
			}
		});
		System.out.println("Client subscribed to /topic/messages");
		
	}
	
	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		exception.printStackTrace();
	}
}
