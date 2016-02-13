package com.g10.portfolio1.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame{

	private static final long serialVersionUID = 3982136114043671567L;
	private JPanel contentPane;
	JTextField username;
	JTextField password;
	private String name;
	private String pass;
	
	//Create main screen and run it
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public Login() {
		//Set Frame and main panel to contentPane
		setTitle("Grade Tracker | Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(getInputPanel());
		mainPanel.add(getButtonPanel());
		
		contentPane.add(mainPanel, BorderLayout.CENTER);
		
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
		password = new JTextField();
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
				pass = password.getText();
				Thread t = new Thread(new LoginRequestor(name, pass));
				t.start();
			}
		});
		
		buttonPanel.add(goButton);
		
		return buttonPanel;
	}

}

class LoginRequestor implements Runnable {
	
	private String username;
	private String password;
	
	LoginRequestor(String un, String pw) {
		username = un;
		password = pw;
	}
	
	public void run() {
		
		Socket socket = null;
		PrintWriter out;
		
		try {
			// attempt connecting to server to login
			socket = new Socket("localhost", 4444);
			// wait for server to be ready to receive data
			Thread.sleep(1000);
			// open output stream to socket
			out = new PrintWriter(socket.getOutputStream());
			out.println(username);
			out.println(password);
			out.flush();
		} catch (InterruptedException e) {
			System.out.println("Thread interrupted");
			e.printStackTrace();
		} catch (UnknownHostException e) {
			System.out.println("Could not locate host");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Problem connecting to host");
			e.printStackTrace();
		}

	}
}
