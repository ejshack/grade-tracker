package com.g10.portfolio1.client;

import java.net.Socket;

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
	// The client's login socket to authenticate with server
	private Socket loginSocket;
	
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
	
	synchronized public Socket getLoginSocket() {
		return loginSocket;
	}
	
	synchronized public void setLoginSocket(Socket s) {
		loginSocket = s;
	}
}
