package org.scrumple.scrumplecore.applications;

public class User {
	private String credentials;
	private String name;
	private int role;
	
	public User(String credential, String name, int role) {
		this.credentials = credential;
		this.name = name;
		this.role = role;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getCredentials() {
		return this.credentials;
	}
	
	public int getRole() {
		return this.role;
	}
}
