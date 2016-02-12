package com.g10.portfolio1.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.AbstractListModel;

public class ServerModel {
	
}

/**
 * Data model for the user list.
 *
 */
class UserListModel extends AbstractListModel<String> {

	private static final long serialVersionUID = 1L;
	private ArrayList<String> userList = null;
	private File userFile = null;
	
	public UserListModel(File file) {
		Scanner scan = null;
		userList = new ArrayList<>();
		userFile = file;
		
		// scans the file and adds users to list
		try {
			scan = new Scanner(file);
			while(scan.hasNextLine())
				userList.add(scan.nextLine());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scan.close();
		}
	}
	
	@Override
	synchronized public String getElementAt(int arg0) {
		return userList.get(arg0);
	}

	@Override
	synchronized public int getSize() {
		return userList.size();
	}
	
	synchronized public void addElement (String arg) {
		userList.add(arg);
		this.fireIntervalAdded(this,userList.size()-1, userList.size()-1);
	}
	
	synchronized public void removeElement (int index) {
		userList.remove(index);
		this.fireIntervalRemoved(this, userList.size()-1, userList.size()-1);
	}
	
	synchronized public File getFile() {
		return userFile;
	}
}
