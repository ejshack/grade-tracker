package com.g10.portfolio1.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.AbstractListModel;

public class ServerModel {
	
}

/**
 * Data model for the user list. Model 
 * implementation for server's MVC pattern.
 *
 */
class UserListModel extends AbstractListModel<String> {

	private static final long serialVersionUID = 1L;
	// Stores usernames of registered users
	ArrayList<String> userList = null;
	// File to store registered user's usernames
	File userFile = null;
	
	public UserListModel(File file) {
		Scanner scan = null;
		userList = new ArrayList<>();
		userFile = file;
		
		// scans the file and adds users to list
		try {
			scan = new Scanner(file);
			while(scan.hasNextLine())
				userList.add(scan.nextLine());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			scan.close();
		}
	}
	
	/**
	 * Returns username string at given index
	 * @param arg0
	 *   index in list of user to return
	 */
	@Override
	synchronized public String getElementAt(int arg0) {
		return userList.get(arg0);
	}

	/**
	 * Returns the number of users
	 */
	@Override
	synchronized public int getSize() {
		return userList.size();
	}
	
	/**
	 * Adds a user to the list
	 * @param arg
	 *   username to add
	 * @return
	 *   true if user was added, false otherwise
	 */
	synchronized public boolean addElement (String arg) {
		boolean userAdded = false;
		if(userList.add(arg))
			userAdded = true;
		this.fireIntervalAdded(this,userList.size()-1, userList.size()-1);
		return userAdded;
	}
	
	/**
	 * Removes a user from the list
	 * @param index
	 *   index of user to remove
	 * @return
	 *   user removed from the list
	 */
	synchronized public String removeElement (int index) {
		String userRemoved = userList.remove(index);
		this.fireIntervalRemoved(this, userList.size()-1, userList.size()-1);
		try {
			Files.deleteIfExists(Paths.get("src\\com\\g10\\portfolio1\\resources\\server\\" + userRemoved + ".txt"));
		} catch (IOException e) {
			System.out.println("User: " + userRemoved + " resource file not found. Could not be removed.");
			e.printStackTrace();
		}
		return userRemoved;
	}
	
	/**
	 * Returns the file of users
	 * @return
	 *   file containing all users
	 */
	synchronized public File getFile() {
		return userFile;
	}
	
	/**
	 * List of users
	 * @return
	 *   list of users
	 */
	synchronized public ArrayList<String> getList() {
		return userList;
	}
}
