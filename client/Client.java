package com.g10.portfolio1.client;


import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;


public class Client {
	
	public static void main(String[] args) {

		//Try to connect with the server
		Socket s = null;
		LoginStatus loginStatus = new LoginStatus();
		
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
		
		//Show main screen
		System.out.println("Logged-in - Main Screen Here");
		// Kill login thread

		//Set socket for main screen and setVisible to true

	}

	public static boolean sendToServer() {
		// TODO add functionality
		return false;
	}

}

class LoginThread extends Thread {

	private Socket mySocket = null;
	private LoginStatus loginStatus;
	
	LoginThread(Socket s, LoginStatus l) {
		mySocket = s;
		loginStatus = l;
	}

	public void run() {
		new Login(mySocket, loginStatus);
	}

}

class LoginStatus {
	
	volatile boolean status = false;
	
	synchronized public boolean getStatus() {
		return status;
	}
	
	synchronized public void setStatus(boolean s) {
		status = s;
	}
}