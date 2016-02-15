package com.g10.portfolio1.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ClientLogin {

	private JPanel contentPane;
	private JTextField username;
	private JPasswordField password;
	private String name;
	private char[] pass;
	private String loginStatus = "REJECTED";
	Socket socket;
	private boolean connected;
	private JFrame frame;
	private LoginStatus lStatus;
	
	/**
	 * Interface for client login
	 * @param s
	 *   socket for connecting to server
	 */
	public ClientLogin(Socket s, LoginStatus l) {

		socket = s;
		lStatus = l;
		
		frame = setupGUI();
		frame.setVisible(true);
	}
	
	/**
	 * Sets up the GUI for the client login.
	 * @return
	 *   JFrame containing login GUI
	 */
	private JFrame setupGUI() {
		connected = false;
		//Set Frame and main panel to contentPane
		JFrame frame = new JFrame();
		frame.setTitle("Grade Tracker | Login");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 400, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(getInputPanel());
		mainPanel.add(getButtonPanel());
		
		contentPane.add(mainPanel, BorderLayout.CENTER);
		
		return frame;
	}
	
	/**
	 * Returns whether the user has been authenticated
	 * with the server or not.
	 * 
	 * @return
	 *   true if authentication successful, false otherwise
	 */
	public boolean isConnected() {
		return connected;
	}
	
	//Create input panel for logging in
	private JPanel getInputPanel() {
		JPanel inputPanel = new JPanel();
		JPanel usernamePanel = new JPanel();
		JPanel passwordPanel = new JPanel();
		
		//Setup username panel with labels
		JLabel lusername = new JLabel("Username:    ");
		username = new JTextField();
		usernamePanel.add(lusername);
		usernamePanel.add(username);
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));

		//Setup password panel with labels
		JLabel lpassword = new JLabel("Password:    ");
		password = new JPasswordField();
		
		passwordPanel.add(lpassword);
		passwordPanel.add(password);
		passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
		
		//Set the max size for the username and password panels
		usernamePanel.setMaximumSize(new Dimension(250, 80));
		passwordPanel.setMaximumSize(new Dimension(250, 80));
		
		//Add username and password panels to inputPanel
		inputPanel.add(usernamePanel);
		inputPanel.add(passwordPanel);
		
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		
		return inputPanel;
	}
	
	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel();
		
		JButton goButton = new JButton("Go");
		goButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				name = username.getText();
				pass = password.getPassword();
				
				lStatus.setName(name);
				requestLogin();
				
				// Exits login if successful, displays message if newly registered or wrong credentials
				if(loginStatus.equals("ACCEPTED")) {
					connected = true;
					lStatus.setStatus(true);
					lStatus.setNew(false);
					frame.setVisible(false);
				} else if(loginStatus.equals("REGISTERED")) {
					JOptionPane.showMessageDialog(null, "Thanks for Registering! Enjoy Grade-Tracker!", 
							"Registration Successful",  JOptionPane.INFORMATION_MESSAGE);
					connected = true;
					lStatus.setStatus(true);
					frame.setVisible(false);
				} else {
					JOptionPane.showMessageDialog(null, "Incorrect username or password", 
							"Invalid Credentials Error",  JOptionPane.ERROR_MESSAGE);
					username.setText("");
					password.setText("");
				}
			}
		});
		
		buttonPanel.add(goButton);
		
		return buttonPanel;
	}

	/**
	 * Passes username and password to
	 * server in attempt to login.
	 */
	private void requestLogin() {
		
		PrintWriter out;
		
		try {
			out = new PrintWriter(socket.getOutputStream());
			out.println(name);
			out.println(pass);
			out.flush();
		} catch (UnknownHostException e) {
			System.out.println("Could not locate host");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Problem connecting to host");
			e.printStackTrace();
		}
		
		waitForValidation();
	}
	
	/**
	 * Waits for login validation from server.
	 */
	private void waitForValidation() {
		
		Scanner in = null;
		
		try {
			in = new Scanner(socket.getInputStream());
			loginStatus = in.nextLine();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
