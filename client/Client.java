package com.g10.portfolio1.client;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class Client {
	
	public static void main(String[] args) {

		//Try to connect with the server
		Socket s = null;
		LoginStatus loginStatus = new LoginStatus();
		
		try {
			s = new Socket("localhost", 4444);
			// wait for server to be ready to receive data
			Thread.sleep(1000);
			loginStatus.setLoginSocket(s);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Cannot connect to server. Please try again later.", 
					"Connection Error",  JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Show login screen, sends credentials to server for validation
		LoginThread loginThread = new LoginThread(loginStatus);
		loginThread.start();
		
		// Waits for user to receive authentication from the server
		while(!loginStatus.getStatus());
		
		//Show main screen
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					MainScreen frame = new MainScreen(loginStatus);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

class LoginThread extends Thread {

	private LoginStatus loginStatus;
	
	LoginThread(LoginStatus l) {
		loginStatus = l;
	}

	public void run() {

		new ClientLogin(loginStatus);
	}

}
