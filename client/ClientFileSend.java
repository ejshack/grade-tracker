package com.g10.portfolio1.client;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientFileSend implements Runnable {
	
	private String clientName;
	private LoginStatus loginStatus;
	private Socket socket;
	// Stops thread when file send is complete
	private boolean sendComplete;
	
	public ClientFileSend(LoginStatus status) {
		
		try {
			socket = new Socket("localhost", 4446);
		} catch (IOException e) {
			System.out.println("Problems establishing connection to server to send resource file...");
			e.printStackTrace();
		}
		clientName = loginStatus.getName();
		sendComplete = false;
	}
	
	public void run() {
		// Stop thread when complete file is sent
		if(!sendComplete) {
			// Scanners to read file
			Scanner readTemp = null;
			// PrintWriter to send file
			PrintWriter writeStream = null;
			
			try {
				
				writeStream = new PrintWriter(socket.getOutputStream());
				writeStream.println(clientName);
				writeStream.flush();
				System.out.println("Sent client name");
	
				readTemp = new Scanner(tempFile);
				
				// Read and send entire file line by line
				while(readTemp.hasNextLine()) {
					writeStream.println(readTemp.nextLine());
				}
				writeStream.println("<FILESENT>");
				writeStream.flush();
				
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				sendComplete = true;
				readTemp.close();
				writeStream.close();
			}
		}
	}
}