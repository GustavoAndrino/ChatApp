package com.example.websocket_demo.client;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import com.example.websocket_demo.Message;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {
	
	private String username;
	private MessageListener messageListener;
	
	public MyStompSessionHandler(MessageListener messageListener, String username) {
		this.username = username;
		this.messageListener = messageListener;
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
						messageListener.onMessageReceive(message);
						System.out.println("Received message: " + message.getUser() + ": " + message.getMessage());
					}
					
				}catch(Exception e) {
					System.out.println("Received unexpected payload type: " + payload.getClass());
				}
			}
		});
		System.out.println("Client subscribed to /topic/messages");
		
		session.subscribe("/topic/users", new StompFrameHandler() {
			
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return new ArrayList<String>().getClass() ;
			}
			
			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				try {
					if(payload instanceof ArrayList) {
						ArrayList<String> activeUsers = (ArrayList<String>) payload;
						messageListener.onActiveUsersUpdated(activeUsers);
						System.out.println("Received active users" + activeUsers);
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
				
			}
			
		});
		System.out.println("Subscribed to /topic/users");
		
		session.send("/app/request-users", "");
		
	}
	
	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		exception.printStackTrace();
	}
}
