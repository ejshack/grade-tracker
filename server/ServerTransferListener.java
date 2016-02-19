package com.g10.portfolio1.server;

import java.io.File;
import java.io.FileNotFoundException;
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
	File resFolder;
	// Stops thread when file send is complete
	private boolean sendComplete;
	
	ServerTransferHandler(Socket s) {
		clientSocket = s;
		sendComplete = false;
	}
	
	public void run() {
		
		// input from client
		Scanner in;
		String clientName;
		
		// Stop thread when complete file is sent
		if(!sendComplete) {
			// PrintWriter to send sem, course, and assignment info
			PrintWriter sendFile;
						
			try {
				in = new Scanner(clientSocket.getInputStream());
				sendFile = new PrintWriter(clientSocket.getOutputStream());
				
				String request = in.nextLine();
				
				if(request.equals("<LIST>")) {
					// Get client name first and get resource file
					clientName = in.nextLine();
					resFolder = new File("src\\com\\g10\\portfolio1\\resources\\server\\" + clientName);
					
					// send all files in resource folder
					sendFiles(resFolder, sendFile);	
					// let client know all files have been sent
					sendFile.println("<COMPLETE>");
					sendFile.flush();
					
				} else if(request.equals("<ASSIGNMENT>")) {
					
					clientName = in.nextLine();
					resFolder = new File("src\\com\\g10\\portfolio1\\resources\\server\\" + clientName);
					String semester = in.nextLine();
					String course = in.nextLine();
					
					sendAssignment(resFolder, sendFile, semester, course);
				}

			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				sendComplete = true;
			}
		}
	}
	
	/**
	 * Sends contents of resource folder to client.
	 */
	private static void sendFiles(File resFolder, PrintWriter pw) {
		
		// get semesters
		File[] semestersAndCourses = resFolder.listFiles();
		PrintWriter sendFile = pw;
		
	    for (File file : semestersAndCourses) {
	        if (file.isDirectory()) {
	        	// send semester tag and name
		    	sendFile.println(file.getName());
		    	sendFile.flush();
	        	
	            sendFiles(file, sendFile);
	            
	        } else {
	        	// send course tag and parent file
	        	sendFile.println(file.getName());
	        	sendFile.flush();
	        }
	    }
	    sendFile.println("<ENDSEMESTER>");
	}
	
	private static void sendAssignment(File resFolder, PrintWriter pw, String sem, String crse) {
		
		// get semesters
		File[] semesters = resFolder.listFiles();
		PrintWriter sendFile = pw;
		
	    for (File file : semesters) {
	        if (file.getName().equals(crse)) {
	        	
	        	// send file contents
	        	Scanner readFile = null;
	        	try {
	    			readFile = new Scanner(file);
	    			while(readFile.hasNextLine()) {
	    				sendFile.println(readFile.nextLine());
	    			}
	    			sendFile.println("<COMPLETE>");
	    		} catch (FileNotFoundException e) {
	    			sendFile.println("<ERROR>");
	    			e.printStackTrace();
	    		} finally {
	            	sendFile.flush();
	            	readFile.close();
	    		}
	        }
	    }
    	
	}
}