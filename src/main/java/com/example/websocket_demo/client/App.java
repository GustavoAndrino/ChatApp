package com.example.websocket_demo.client;

import javax.swing.SwingUtilities;

public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ClientGUI clientGUI = new ClientGUI("TapTap");
				clientGUI.setVisible(true);
			}
		});
	}
}
