package com.example.websocket_demo.client;

import java.util.ArrayList;

import com.example.websocket_demo.Message;

public interface MessageListener {
	void onMessageReceive(Message message);
	void onActiveUsersUpdated(ArrayList<String> users);

}
