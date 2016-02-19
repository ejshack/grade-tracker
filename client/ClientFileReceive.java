package com.g10.portfolio1.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JOptionPane;

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
			
			try {
				
				pw = new PrintWriter(socket.getOutputStream());
				pw.println(clientName);
				pw.flush();
				System.out.println("Sent client name");
	
				readStream = new Scanner(socket.getInputStream());
				
				
				
				
				// put this in receive method
				
				// semester name, directory of semester
				HashMap<String, File> semesterFiles = new HashMap<>();
				HashMap<String, File> courseFiles = new HashMap<>();
				ArrayList<String> readList = new ArrayList<>();
				int index = 0;
				
				String input = readStream.nextLine();
				PrintWriter writeTemp = null;

				
				while(!input.equals("<COMPLETE>")) {
					
					// add semester directory
					if(input.equals("<SEMESTER>")) {
						// get semester name
						String semester = readStream.nextLine();
					
						// create directory for semester
						Path temp = Files.createTempDirectory(
								Paths.get("src\\com\\g10\\portfolio1\\resources\\client\\"
										+tempFile.getName()), semester);
						
						// create reference
						File semFile = new File(temp.toString());
						semFile.deleteOnExit();
						semesterFiles.put(semester, semFile);
						
					} else if(input.equals("<COURSE>")) {

						// get course name and semester from
						String course = readStream.nextLine();
						String semester = readStream.nextLine();
						
						// create file for course
						File courseFile = File.createTempFile(course, ".tmp", semesterFiles.get(semester));
						courseFile.deleteOnExit();
						courseFiles.put(course, courseFile);
						
						// read file contents
						while(!readList.get(index).equals("<FILESENT>")) {
							// perform action if error reading file
							if(readList.get(index).equals("<ERROR>")) {
								JOptionPane.showMessageDialog(null, "Error receiving course information. Please try again later.", 
										"Retrieve Course Error",  JOptionPane.ERROR_MESSAGE);
								System.exit(-1);
							}
							readList.add(readStream.nextLine());
							++index;
						}
						writeTemp = new PrintWriter(courseFile);
						// Write list to temp file
						for(String s : readList) {
							writeTemp.println(s);
						}
						writeTemp.close();
						
						// remove used items from list
						readList.clear();
						index = 0;
					}
					
					// prior setup
//					while(!readList.get(index).equals("<FILESENT>")) {
//						readList.add(readStream.nextLine());
//						System.out.println(readList.get(index));
//						++index;
//					}
//					// Remove termination character
//					readList.remove(index);
					
//					// Write list to temp file
//					for(String s : readList) {
//						writeTemp.println(s);
//					}
				}
				
				
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				receiveComplete = true;
				readStream.close();
//				writeTemp.close();
			}
		}
	}
	
	/**
	 * Receive semester and class contents from server.
	 */
//	private static void receiveFiles(File resFolder, Scanner in) {
//		
//		// get semesters
//		File[] semestersAndCourses = resFolder.listFiles();
//		PrintWriter sendFile = pw;
//		
//	    for (File file : semestersAndCourses) {
//	        if (file.isDirectory()) {
//	        	// send semester tag and name
//		    	sendFile.println("<SEMESTER>");
//		    	sendFile.println(file.getName());
//		    	sendFile.flush();
//	        	
//	            sendFiles(file, sendFile);
//	        } else {
//	        	// send course tag and parent file
//	        	sendFile.println("<COURSE>");
//	        	sendFile.println(file.getName());
//	        	sendFile.println("<PARENT>");
//	        	sendFile.println(file.getParentFile().getName());
//	        	
//	        	// send file contents
//	        	Scanner readFile = null;
//	        	try {
//					readFile = new Scanner(file);
//					while(readFile.hasNextLine()) {
//						sendFile.println(readFile.nextLine());
//					}
//					sendFile.println("<FILESENT>");
//				} catch (FileNotFoundException e) {
//					sendFile.println("<ERROR>");
//					e.printStackTrace();
//				} finally {
//		        	sendFile.flush();
//		        	readFile.close();
//				}
//	        }
//	    }
//	}
}
