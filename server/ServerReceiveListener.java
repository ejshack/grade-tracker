package com.g10.portfolio1.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Thread to open socket to listen for client connections
 * for receiving resource files. Resource files are saved
 * permanently on the server.
 *
 */
public class ServerReceiveListener implements Runnable {
	
	// Socket used to pass resource file to client
	ServerSocket serverSocket;
	Socket clientSocket;
	
	public ServerReceiveListener() {
		serverSocket = null;
		clientSocket = null;
		
		try {
			serverSocket = new ServerSocket(4446);
		} catch(IOException e) {
			System.out.println("Couldn't listen on 4446...");
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		while(true) {
			
			try {
				System.out.println("Listening for resource file receive requests on 4446...");
				// Start thread to send client's resource file after receiving connection
				clientSocket = serverSocket.accept();
				Thread handlerThread = new Thread(new ServerReceiveHandler(clientSocket));
				handlerThread.start();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

}

/**
 * Handles individual clients to receive their resource
 * file before they log off. Resource file is saved
 * on the server permanently.
 *
 */
class ServerReceiveHandler implements Runnable {
	
	Socket clientSocket;
	File resFile;
	// Stops thread when file send is complete
	private boolean receiveComplete;
	
	ServerReceiveHandler(Socket s) {
		clientSocket = s;
		receiveComplete = false;
	}
	
	public void run() {
		
		// Stop thread when complete file is received
		while(!receiveComplete) {
			// Scanners to read stream
			Scanner readStream = null;
			// PrintWriter to write file
			PrintWriter writeFile = null;
			
			ArrayList<String> readList;
			String clientName;
			String semester;
			String course;
			int index = 0;
			
			try {
				readStream = new Scanner(clientSocket.getInputStream());
				// Get client name first and get resource file
				clientName = readStream.nextLine();
				semester = readStream.nextLine();
				course = readStream.nextLine();
				
				resFile = new File("src\\com\\g10\\portfolio1\\resources\\server\\" + clientName + semester + course + ".csv");
				writeFile = new PrintWriter(resFile);
				
				readList = new ArrayList<>();
				readList.add(readStream.nextLine());
				
				while(!readList.get(index).equals("<COMPLETE>")) {
					readList.add(readStream.nextLine());
					System.out.println(readList.get(index));
					++index;
				}
				// Remove termination character
				readList.remove(index);
				
				// Write list to resource file
				for(String s : readList) {
					writeFile.println(s);
				}
				
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				if(clientSocket.isClosed()) {
					receiveComplete = true;
					readStream.close();
					writeFile.close();
				}
			}
		}
	}

}
