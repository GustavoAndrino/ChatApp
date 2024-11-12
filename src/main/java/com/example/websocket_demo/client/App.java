package com.example.websocket_demo.client;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;

public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ClientGUI clientGUI = null;
				try {
					clientGUI = new ClientGUI("TapTap");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				clientGUI.setVisible(true);
			}
		});
	}
}
