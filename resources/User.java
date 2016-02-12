package com.g10.portfolio1.resources;

import java.io.File;

/**
 * Represents a user of the program and identifies 
 * their username and resource file.
 *
 */
public class User {
	
	private String username;
	private File resourceFile;

	/**
	 * Creates a user with the specified 
	 * @param name
	 * @param file
	 */
	public User(String name, File file) {
		username = name;
		resourceFile = file;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public File getResourceFile() {
		return resourceFile;
	}

	public void setResourceFile(File resourceFile) {
		this.resourceFile = resourceFile;
	}

}
