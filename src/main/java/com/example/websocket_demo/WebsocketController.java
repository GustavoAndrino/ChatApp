package com.example.websocket_demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {
	private final SimpMessagingTemplate messagingTemplate;
	private final WebSocketSessionManager sessionManager;
	
	@Autowired
	public WebsocketController(SimpMessagingTemplate messagingTemplate, WebSocketSessionManager sessionManager) {
		this.messagingTemplate = messagingTemplate;
		this.sessionManager = sessionManager;
	}
	
	@MessageMapping("/message")
	public void handleMessage(Message message) {
		System.out.println("Received message from user: " + message.getUser() + ": " + message.getMessage());
		messagingTemplate.convertAndSend("/topic/messages", message);
		System.out.println("Sent message to /topic/messages: " + message.getUser() + ": " + message.getMessage());
	}
	
	@MessageMapping("/connect")
	public void connectUser(String username) {
		sessionManager.addUser(username);
		sessionManager.broadcastActiveUsernames();
		System.out.println(username + " connected");
	}
	
	@MessageMapping("/disconnect")
	public void disconnectUser(String username) {
		sessionManager.removeUser(username);
		sessionManager.broadcastActiveUsernames();
		System.out.println(username + " disconnected");
	}
}