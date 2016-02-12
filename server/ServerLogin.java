package com.g10.portfolio1.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Handles client login requests.
 *
 */
public class ServerLogin  {
	
	UserListModel listModel;
//	private Map<Socket, String> connections;
	ServerSocket serverSocket;
	
	public ServerLogin(UserListModel model) {
		listModel = model;
		openLoginSocket();
		listen();
	}
	
	/**
	 * Open socket to service login requests.
	 */
	private void openLoginSocket() {
		
//		connections = new HashMap<>();
		serverSocket = null;
		
		try {
			// Create server socket
			serverSocket = new ServerSocket(4444);
		} catch (IOException e) {
			System.out.println("Could not listen on port: 4444");
			System.exit(-1);
		}
	}

	/**
	 * Listen for clients to connect and 
	 * fork new thread for each client.
	 */
//	@SuppressWarnings("resource")
	private void listen() {
		
		// Wait for connections
		while (true) {
			
			Socket clientSocket = null;
			
			try {
				System.out.println("Listening for connections on 4444...");
				clientSocket = serverSocket.accept();
				System.out.println("Remote Address: " + clientSocket.getInetAddress());
				Thread t = new Thread(new LoginHandler(clientSocket, listModel));
				t.start();
			} catch (IOException e) {
				System.out.println("Accept failed: 4444");
				System.exit(-1);
			}
		}
	}

}


class LoginHandler implements Runnable {
	// Handles connection to client
	Socket s;
	UserListModel listModel;
	// Username of connected client
	private String name;
	private String pass;
	
	LoginHandler(Socket s, UserListModel lm) {
		this.s = s;
		listModel = lm;
	}
	
	public void run() {
		// Scanner to read input from client
		Scanner in;
	//	ArrayList<String> sendList = new ArrayList<>();
//		String clientMessage;
	//	ArrayList<Socket> socketList;
		
		try {
			in = new Scanner(s.getInputStream());
			name = in.nextLine();
			pass = in.nextLine();
//			clientMessage = in.nextLine();
	//		while(in.hasNextLine())
	//			sendList.add(in.nextLine());
			System.out.println(name);
			System.out.println(pass);
			listModel.addElement(name);
//			System.out.println(clientMessage);
	//		for(String s : sendList) {
	//			
	//		}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
//  ****** GOOD WAY TO PURPOSELY KILL A QUICK USE THREAD ********
//	
//	class MyThread extends Thread
//	{
//	  volatile boolean finished = false;
//
//	  public void stopMe()
//	  {
//	    finished = true;
//	  }
//
//	  public void run()
//	  {
//	    while (!finished)
//	    {
//	      //do dirty work
//	    }
//	  }
//	}
	
	//void printSocketInfo(Socket s) {
	//	System.out.print("Socket on Server " + Thread.currentThread() + " ");
	//	System.out.print("Server socket Local Address: " + s.getLocalAddress()
	//			+ ":" + s.getLocalPort());
	//	System.out.println("  Server socket Remote Address: "
	//			+ s.getRemoteSocketAddress());
	//}
}
