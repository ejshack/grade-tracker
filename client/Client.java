package com.g10.portfolio1.client;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;


public class Client {
	
	public static void main(String[] args) {

		//Try to connect with the server
		Socket s = null;
		LoginStatus loginStatus = new LoginStatus();

		String username;
		
		try {
			s = new Socket("localhost", 4444);
			// wait for server to be ready to receive data
			Thread.sleep(1000);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Cannot connect to server. Please try again later.", 
					"Connection Error",  JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Show login screen, sends credentials to server for validation
		LoginThread loginThread = new LoginThread(s, loginStatus);
		loginThread.start();
		
		// Waits for user to receive authentication from the server
		while(!loginStatus.getStatus());

		username = loginStatus.getName();
		
		//Show main screen
		System.out.println("Logged-in - Main Screen Here");

		//Set socket for main screen and setVisible to true

	}

}

class LoginThread extends Thread {

	Socket mySocket = null;

	private LoginStatus loginStatus;
	
	LoginThread(Socket s, LoginStatus l) {
		mySocket = s;
		loginStatus = l;
	}

	public void run() {

		new ClientLogin(mySocket, loginStatus);
	}

}
