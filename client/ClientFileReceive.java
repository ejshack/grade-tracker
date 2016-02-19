package com.g10.portfolio1.client;

import java.io.File;
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
	private String clientName;
	private String semester;
	private String course;
	// request either semester and course listing or specific assignment
	private String request;
	
	public ClientFileReceive(String name) {
		
		clientName = name;
		// request semester and course listing
		request = "<LIST>";
		connect();
	}
	
	public ClientFileReceive(String name, String sem, String course) {
		
		clientName = name;
		semester = sem;
		this.course = course;
		// request specific assignment
		request = "<ASSIGNMENT>";
		connect();
	}
	
	/**
	 * Opens connection to server.
	 */
	private void connect() {
		
		try {
			socket = new Socket("localhost", 4445);
		} catch (IOException e) {
			System.out.println("Problems establishing connection to server for resource files...");
			e.printStackTrace();
		}
	}
	
	public void run() {

		PrintWriter pw;
		// Scanners to read file
		Scanner readStream = null;
		
		try {
			
			pw = new PrintWriter(socket.getOutputStream());
			readStream = new Scanner(socket.getInputStream());
			
			if(request.equals("<LIST>")) {
				pw.println(request);
				pw.println(clientName);
				pw.flush();
				
				
				
				
			} else {
				pw.println(request);
				pw.println(clientName);
				pw.println(semester);
				pw.println(course);
				pw.flush();
				
				
				
			}
				
			System.out.println("Sent client name");
	
			
				
				
				
				
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
//						String semester = readStream.nextLine();
					
	//					// create directory for semester
	//					Path temp = Files.createTempDirectory(
	//							Paths.get("src\\com\\g10\\portfolio1\\resources\\client\\"
	//									+tempFile.getName()), semester);
						
						// create reference
//						File semFile = new File(temp.toString());
//						semFile.deleteOnExit();
//						semesterFiles.put(semester, semFile);
						
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
					
				}
					
					
				} catch(IOException e) {
					e.printStackTrace();
				} finally {
					readStream.close();
				}
			}
//		}
}
