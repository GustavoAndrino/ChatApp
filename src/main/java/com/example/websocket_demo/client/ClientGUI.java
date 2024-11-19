package com.example.websocket_demo.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.ScrollPane;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.example.websocket_demo.Message;

public class ClientGUI extends JFrame implements MessageListener{
	private JPanel connectedUsersPanel, messagePanel;
	private MyStompClient myStompClient;
	private String username;
	private JScrollPane messagePanelScrollPane;
	
	public ClientGUI(String username) throws InterruptedException, ExecutionException {
		super("User: " + username);
		this.username = username;
		myStompClient = new MyStompClient(this, username);
		
		setSize(1218, 685);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int option = JOptionPane.showConfirmDialog(ClientGUI.this, "Do you really want to leave ? =(",
						"Exit", JOptionPane.YES_NO_OPTION);
				
				if(option == 	JOptionPane.YES_OPTION) {
					myStompClient.disconnectUser(username);
					ClientGUI.this.dispose();
				}
			}
		});
		
		addComponentListener(new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
				updateMessageSize();
				//TODO
			}
		});
		
		getContentPane().setBackground(Utilities.PRIMARY_COLOR);	
		addGuiComponents();	
}
	
	private void addGuiComponents() {
		addConnectedUsersComponents();
		addChatComponents();
	}
	
	private void addConnectedUsersComponents() {
		connectedUsersPanel = new JPanel();
		connectedUsersPanel.setBorder(Utilities.addPadding(10, 10, 10, 10));
		connectedUsersPanel.setLayout(new BoxLayout(connectedUsersPanel, BoxLayout.Y_AXIS));
		connectedUsersPanel.setBackground(Utilities.SECONDARY_COLOR);
		connectedUsersPanel.setPreferredSize(new Dimension(200, getHeight()));
		
		JLabel connectedUsersLabel = new JLabel("Connected Users");
		connectedUsersLabel.setFont(new Font("Inter", Font.BOLD, 18));
		connectedUsersLabel.setForeground(Utilities.TEXT_COLOR);
		connectedUsersPanel.add(connectedUsersLabel);
		
		add(connectedUsersPanel, BorderLayout.WEST);
	}
	
	private void addChatComponents() {
		JPanel chatPanel = new JPanel();
		chatPanel.setLayout(new BorderLayout());
		chatPanel.setBackground(Utilities.TRANSPARENT_COLOR);
		
		messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		messagePanel.setBackground(Utilities.TRANSPARENT_COLOR);
		
		messagePanelScrollPane = new JScrollPane(messagePanel);
		messagePanelScrollPane.setBackground(Utilities.TRANSPARENT_COLOR);
		messagePanelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		messagePanelScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		messagePanelScrollPane.getViewport().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				revalidate();
				repaint();
			}
		});
		
		chatPanel.add(messagePanelScrollPane, BorderLayout.CENTER);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setBorder(Utilities.addPadding(10, 10, 10, 10));
		inputPanel.setLayout(new BorderLayout());
		inputPanel.setBackground(Utilities.TRANSPARENT_COLOR);
		
		JTextField inputField = new JTextField();
		inputField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					String input = inputField.getText();
					
					if(input.isEmpty()) return;
					
					inputField.setText("");
		
					myStompClient.sendMessage(new Message(username,input));
				}
			}
		});
		inputField.setBackground(Utilities.SECONDARY_COLOR);
		inputField.setForeground(Utilities.TEXT_COLOR);
		inputField.setBorder(Utilities.addPadding(0, 10, 0, 10));
		inputField.setFont(new Font("Inter", Font.PLAIN, 16));
		inputField.setPreferredSize(new Dimension(inputPanel.getWidth(), 50));
		inputPanel.add(inputField, BorderLayout.CENTER);
		chatPanel.add(inputPanel, BorderLayout.SOUTH);
		
		add(chatPanel, BorderLayout.CENTER);
	}
	
	private JPanel createChatMessageComponent(Message message) {
		JPanel chatMessage = new JPanel();
		chatMessage.setBackground(Utilities.TRANSPARENT_COLOR);
		chatMessage.setLayout(new BoxLayout(chatMessage, BoxLayout.Y_AXIS));
		chatMessage.setBorder(Utilities.addPadding(20, 20, 10, 20));
		
		JLabel usernameLabel = new JLabel(message.getUser());
		usernameLabel.setForeground(Utilities.TEXT_COLOR);
		usernameLabel.setFont(new Font("Inter", Font.BOLD, 18));
		chatMessage.add(usernameLabel);
		
		JLabel messageLabel = new JLabel();
		messageLabel.setText("<html>" +
                "<body style='width:" + (0.60 * getWidth()) + "'px>" +
                    message.getMessage() +
                "</body>"+
        "</html>");
		messageLabel.setForeground(Utilities.TEXT_COLOR);
		messageLabel.setFont(new Font("Inter", Font.PLAIN, 18));
		chatMessage.add(messageLabel);
		
		return chatMessage;
	}

	@Override
	public void onMessageReceive(Message message) {
		messagePanel.add(createChatMessageComponent(message));
		revalidate();
		repaint();
		
		messagePanelScrollPane.getVerticalScrollBar().setValue(Integer.MAX_VALUE);
	}

	@Override
	public void onActiveUsersUpdated(ArrayList<String> users) {
		if(connectedUsersPanel.getComponents().length >= 2) {
			connectedUsersPanel.remove(1);
		}
		
		JPanel userListPanel = new JPanel();
		userListPanel.setBackground(Utilities.TRANSPARENT_COLOR);
		userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
		
		for(String user : users) {
			JLabel username = new JLabel();
			username.setText(user);
			username.setForeground(Utilities.TEXT_COLOR);
			username.setFont(new Font("Inter", Font.BOLD, 16));
			userListPanel.add(username);
		}
		
		connectedUsersPanel.add(userListPanel);
		revalidate();
		repaint();
	}
	
	private void updateMessageSize() {
		for(int i = 0; i < messagePanel.getComponents().length; i++) {
			Component component = messagePanel.getComponent(i);
			if(component instanceof JPanel) {
				JPanel chatMessage = (JPanel) component;
				if(chatMessage.getComponent(1) instanceof JLabel) {
					JLabel messageLabel = (JLabel) chatMessage.getComponent(1);
					messageLabel.setText("<html>" +
							"<body style='width:" + (0.60 * getWidth()) + "'px>" +
								messageLabel.getText() +
							"</body>"+
						"</html>");
				}
			}
		}
	}
	
}
