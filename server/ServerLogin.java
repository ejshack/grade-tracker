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
	
	private UserListModel listModel;
//	private Map<Socket, String> connections;
	ServerSocket serverSocket;
	
	public ServerLogin(UserListModel model) {
		listModel = model;
		openLoginSocket();
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
		listen();
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
				clientSocket = serverSocket.accept();
				Thread t = new Thread(new LoginHandler(clientSocket));
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
	// Username of connected client
	String name;
	
	LoginHandler(Socket s) {
		this.s = s;
		Scanner in;
		try {
			in = new Scanner(s.getInputStream());
			name = in.nextLine();
			in.close();
			System.out.println(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		// Scanner to read input from client
		Scanner in;
	//	ArrayList<String> sendList = new ArrayList<>();
		String clientMessage;
	//	ArrayList<Socket> socketList;
		
		try {
			in = new Scanner(s.getInputStream());
			clientMessage = in.nextLine();
	//		while(in.hasNextLine())
	//			sendList.add(in.nextLine());
			// Send messages to selected users
			System.out.println(name);
			System.out.println(clientMessage);
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
