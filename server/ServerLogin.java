package com.g10.portfolio1.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
	private String loginStatus;
	
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
			// validates user credentials and returns response
			loginStatus = validateUser();
			// sends response to client
			informClient(loginStatus);
			
			if(loginStatus.equals("ACCEPTED")) {
				
				
				// TODO - start sending file if so
			
				
			} else if(loginStatus.equals("REGISTERED")){
				// creates client resource file, adds username
				//   to list, adds credentials to password file
				createClient();
			}
			
			
				// TODO - start client handler
			
			
		} catch (IOException e) {
			e.printStackTrace();
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
		
		File passFile = new File("src\\com\\g10\\portfolio1\\resources\\server\\passwords.txt");
		Scanner lineIn = null;
		Scanner wordIn = null;
		// login status reply, auto-register if not currently a user
		String status = "REGISTERED";
		
		try {
			// line of password file containing a username and password
			lineIn = new Scanner(passFile);
			
			while(lineIn.hasNextLine()) {
				
				wordIn = new Scanner(lineIn.nextLine());
				// checks attempts to match username
				if(wordIn.next().equals(name)) {
					// sets reply to user whether password is match or not
					if(wordIn.next().equals(pass)) {
						status = "ACCEPTED";
					} else {
						status = "REJECTED";
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(wordIn != null)
				wordIn.close();
			if(lineIn != null)
				lineIn.close();
		}
		return status;
	}
	
	/**
	 * Adds user to user list and creates 
	 * their resource file on the server.
	 */
	private void createClient() {
		// username and password
		List<String> lines = Arrays.asList(name + " " + pass);
		// adds username to list
		listModel.addElement(name);
		
		try {
			// creates resource file for user if not already created
			File userRes = new File("src\\com\\g10\\portfolio1\\resources\\server\\" + name + ".txt");
			userRes.createNewFile();
			// adds user credentials to password file
			Path file = Paths.get("src\\com\\g10\\portfolio1\\resources\\server\\passwords.txt");
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
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

}
