package com.g10.portfolio1.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Thread to open socket to listen for client connections
 * for transferring resource files. Resource files are saved
 * temporarily on the client's machine while they
 * are logged in and using the application.
 *
 */
public class ServerTransferListener implements Runnable {
	
	// Socket used to pass resource file to client
	ServerSocket serverSocket;
	Socket clientSocket;
	File resfile;
	
	ServerTransferListener() {
		serverSocket = null;
		clientSocket = null;
		
		try {
			serverSocket = new ServerSocket(4445);
		} catch(IOException e) {
			System.out.println("Couldn't listen on 4445...");
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		while(true) {
			
			try {
				System.out.println("Listening for resource file transfer requests on 4445...");
				// Start thread to send client's resource file after receiving connection
				clientSocket = serverSocket.accept();
				Thread handlerThread = new Thread(new ServerTransferHandler(clientSocket));
				handlerThread.start();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

}

/**
 * Handles individual clients to transfer resource file
 *
 */
class ServerTransferHandler implements Runnable {
	
	Socket clientSocket;
	File resFile;
	// Stops thread when file send is complete
	private boolean sendComplete;
	
	ServerTransferHandler(Socket s) {
		clientSocket = s;
		sendComplete = false;
	}
	
	public void run() {
		
		Scanner in;
		String clientName;
		// Stop thread when complete file is sent
		if(!sendComplete) {
			// Scanners to read file
			Scanner readFile = null;
			// PrintWriter to send file
			PrintWriter sendFile;
						
			try {
				in = new Scanner(clientSocket.getInputStream());
				// Get client name first and get resource file
				System.out.println("Listening for name");
				clientName = in.nextLine();
				System.out.println("Client name received");
				resFile = new File("src\\com\\g10\\portfolio1\\resources\\server\\" + clientName + ".txt");
				
				readFile = new Scanner(resFile);
				sendFile = new PrintWriter(clientSocket.getOutputStream());
				// Read and send entire file line by line
				while(readFile.hasNextLine()) {
					sendFile.println(readFile.nextLine());
				}
				sendFile.println("<FILESENT>");
				sendFile.flush();
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				sendComplete = true;
				readFile.close();
			}
		}
	}
}