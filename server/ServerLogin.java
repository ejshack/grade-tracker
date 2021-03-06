package com.g10.portfolio1.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles client login requests.
 *
 */
public class ServerLogin implements Runnable  {
	
	UserListModel listModel;
	PassListModel passModel;
	ServerSocket serverSocket;
	
	public ServerLogin(UserListModel uModel, PassListModel pModel) {
		
		listModel = uModel;
		passModel = pModel;
		
		// Open socket to service login requests
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
	public void run() {
		
		// Wait for connections
		while (true) {
			// Represents clients socket on server's side
			Socket clientSocket = null;
			
			try {

				System.out.println("Listening for login attempts on 4444...");

				// Blocks until accepts connection
				clientSocket = serverSocket.accept();
				// Forks a thread to handle new/returning client
				Thread t = new Thread(new LoginHandler(clientSocket, listModel, passModel));
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
	PassListModel passModel;

	private String name;
	private String pass;
	private String loginStatus;
	private boolean loginComplete;
	
	LoginHandler(Socket s, UserListModel lm, PassListModel pm) {
		this.s = s;
		listModel = lm;
		passModel = pm;
		loginComplete = false;
	}
	
	public void run() {
		
		// Stop handler after client has logged in
		while(!loginComplete) {
			// Scanner to read input from client
			Scanner in;
			
			try {
				in = new Scanner(s.getInputStream());
				name = in.nextLine();
				pass = in.nextLine();
				
				// validates user credentials and returns response
				loginStatus = validateUser();
				// sends response to client
				informClient(loginStatus);
				
				// creates client resource file, adds username
				//   to list, adds credentials to password file
				if(loginStatus.equals("REGISTERED")){
					
					createClient();
					loginComplete = true;
				} else if(loginStatus.equals("ACCEPTED")) {
					loginComplete = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Sends client validation response.
	 * 
	 * @param status
	 *   validation response for client
	 */
	private void informClient(String status) {
		
		PrintWriter out;
		
		try {
			out = new PrintWriter(s.getOutputStream());
			out.println(status);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks password file to see if already a current
	 * user and attempts to validate credentials.
	 * 
	 * @return
	 *   Validation response to send to user
	 */
	private String validateUser() {
		
		// login status reply, auto-register if not currently a user
		String status = "REGISTERED";
		ArrayList<String> userList = listModel.getList();
		
		int count = userList.size();
		int index = -1;
		
		// search user list for name
		for(int i = 0; i < count; ++i) {
			if(name.equals(userList.get(i))) {
				index = i;
				break;
			}
		}
		
		// if found, check if password is correct
		if(index >= 0) {
			if(pass.equals(passModel.getElementAt(index)))
				status = "ACCEPTED";
			else
				status = "REJECTED";
		}
		return status;
	}
	
	/**
	 * Adds user to user list and creates 
	 * their resource file on the server.
	 */
	private void createClient() {

		// adds username and password to lists
		listModel.addElement(name);
		passModel.addElement(pass);
		
		try {
			// creates resource file for user if not already created
			Files.createDirectory(Paths.get("src\\com\\g10\\portfolio1\\resources\\server\\" + name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
