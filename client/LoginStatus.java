package com.g10.portfolio1.client;

import java.io.File;

/**
 * Contains information to be shared
 * about a client's login status.
 *
 */
public class LoginStatus {
	
	// True if client is logged in, false otherwise
	private boolean status = false;
	// True if client is a new client, false if returning client
	private boolean newClient = true;
	// The name of the client
	private String clientName;
	// The client's temp file stored on client machine
	private File tempFile;
	
	synchronized public boolean getStatus() {
		return status;
	}
	
	synchronized public void setStatus(boolean s) {
		status = s;
	}
	
	synchronized public boolean isNew() {
		return newClient;
	}
	
	synchronized public void setNew(boolean isNew) {
		newClient = isNew;
	}
	
	synchronized public String getName() {
		return clientName;
	}
	
	synchronized public void setName(String name) {
		clientName = name;
	}
	
	synchronized public File getUserTemp() {
		return tempFile;
	}
	
	synchronized public void setUserTemp(File t) {
		tempFile = t;
	}
}
