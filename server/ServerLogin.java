package com.g10.portfolio1.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Handles client login requests.
 *
 */
public class ServerLogin  {
	
	UserListModel listModel;
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
	private void listen() {
		
		// Wait for connections
		while (true) {
			// Represents clients socket on server's side
			Socket clientSocket = null;
			
			try {
				System.out.println("Listening for connections on 4444...");
				// Blocks until accepts connection
				clientSocket = serverSocket.accept();
				// Forks a thread to handle new/returning client
				Thread t = new Thread(new LoginHandler(clientSocket, listModel));
				t.start();
			} catch (IOException e) {
				System.out.println("Accept failed: 4444");
				System.exit(-1);
			}
		}
	}

}

/**
 * Thread to handle new and returning
 * clients when logging in.
 *
 */
class LoginHandler implements Runnable {
	// Handles connection to client
	Socket s;
	UserListModel listModel;

	private String name;
	private String pass;
	
	LoginHandler(Socket s, UserListModel lm) {
		this.s = s;
		listModel = lm;
	}
	
	public void run() {
		// Scanner to read input from client
		Scanner in;
		
		try {
			in = new Scanner(s.getInputStream());
			name = in.nextLine();
			pass = in.nextLine();

			System.out.println(name);
			System.out.println(pass);
			
			if(isCurrentUser()) {
				System.out.println("Already current user");
				// TODO - validate user
				//		- send error if not
				//		- start sending file if so
				//		- start client handler
			} else {
				createClient();
				// TODO - start client handler
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds user to user list and creates 
	 * their resource file on the server.
	 */
	private void createClient() {
		
		List<String> lines = Arrays.asList(name, pass);
		
		listModel.addElement(name);
		
		try {
			Path file = Paths.get("src\\com\\g10\\portfolio1\\resources\\server\\" + name + ".txt");
//					"C:\\Users\\ejshackelford\\java\\workspace\\coms319\\src\\com\\g10\\portfolio1\\resources\\server\\" + name + ".txt");
//			"C:\\Users\\Brody\\Desktop\\iastate\\Spring2016\\ComS319\\Lab2-Swing\\src\\com\\g10\\portfolio1\\resources\\server\\users.txt");name + ".txt");
			Files.write(file, lines, Charset.forName("UTF-8"));
			// Used to append to file
			//Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isCurrentUser() {
		
		ArrayList<String> users = listModel.getList();
		
		for (int i = 0; i < users.size(); ++i) {
			if (name.equals(users.get(i)))
				return true;
		}
		return false;
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

}
