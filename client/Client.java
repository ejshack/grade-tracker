package com.g10.portfolio1.client;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Client {
	
	public static void main(String[] args) {
		//Try to connect with the server
		Socket s = null;
		// Users login status
//		LoginStatus userStatus = new LoginStatus();
		
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
		Login login = new Login(s);
		
		// Waits for user to receive authentication from the server
		while(!login.getUserStatus());
		
		//Show main screen
		
	}

	public static boolean sendToServer() {
		//TODO add functionality
		return false;
	}
}
