package com.g10.portfolio1.client;

import java.net.Socket;

public class Client {

	public static void main(String[] args) {
		// Try to connect with the server
		LoginThread loginThread = new LoginThread();

		// Show login screen
		
		// Kill login thread
		// Show main screen
		while(loginThread.getSocket() == null);
		
		//Set socket for main screen and setVisible to true

	}

	public static boolean sendToServer() {
		// TODO add functionality
		return false;
	}

}

class LoginThread extends Thread {

	private Socket mySocket = null;

	public void run() {
		Login login = new Login();
		while (!login.isConnected());
		
		// Get connected socket from login
		//mySocket = login.getSocket();
	}
	
	public Socket getSocket() {
		return mySocket;
	}

}
