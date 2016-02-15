package com.g10.portfolio1.client;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientFileReceive implements Runnable {
	
	Socket socket = null;
	File tempFile;
	String clientName;
	// Stops thread when file send is complete
	private boolean receiveComplete;
	
	public ClientFileReceive(File f, String name) {
		
		try {
			socket = new Socket("localhost", 4445);
		} catch (IOException e) {
			System.out.println("Problems establishing connection to server for resource file...");
			e.printStackTrace();
		}
		tempFile = f;
		clientName = name;
		receiveComplete = false;
	}
	
	public void run() {
		System.out.println(tempFile.getAbsolutePath());
		// Stop thread when complete file is sent
		if(!receiveComplete) {
			PrintWriter pw;
			// Scanners to read file
			Scanner readStream = null;
			// PrintWriter to send file
			PrintWriter writeTemp = null;
			ArrayList<String> readList;
			int index = 0;
			
			try {
				
				pw = new PrintWriter(socket.getOutputStream());
				pw.println(clientName);
				pw.flush();
				System.out.println("Sent client name");
	
				readStream = new Scanner(socket.getInputStream());
				writeTemp = new PrintWriter(tempFile);
				
				readList = new ArrayList<>();
				readList.add(readStream.nextLine());
				
				while(!readList.get(index).equals("<FILESENT>")) {
					readList.add(readStream.nextLine());
					System.out.println(readList.get(index));
					++index;
				}
				// Remove termination character
				readList.remove(index);
				
				// Write list to temp file
				for(String s : readList) {
					writeTemp.println(s);
			}
				
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				receiveComplete = true;
				readStream.close();
				writeTemp.close();
			}
		}
	}
}
