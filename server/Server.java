package com.g10.portfolio1.server;

public class Server {

	/**
	 * Launch the server application.
	 */
	public static void main(String[] args) {
		
		// Opens server GUI and handles logon requests
		new ServerView();
		
		// Handles resource file transfer requests
		Thread transferThread = new Thread(new ServerTransferListener());
		transferThread.start();
		
		// Handles resource file receive requests
		Thread receiveThread = new Thread(new ServerReceiveListener());
		receiveThread.start();
	}

}
