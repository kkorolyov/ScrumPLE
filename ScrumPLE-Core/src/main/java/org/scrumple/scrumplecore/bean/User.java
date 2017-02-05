package org.scrumple.scrumplecore.bean;

import org.scrumple.scrumplecore.auth.Credentials;

public class User {
	private Credentials credentials;
	private Role role;
	
	public User(){}
	public User(Credentials credentials, Role role) {
		this.credentials = credentials;
		this.role = role;
	}

	public Credentials getCredentials() {
		return credentials;
	}
	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public Role getRole() {
		return this.role;
	}
	public void setRole(Role roleIDX) {
		this.role = roleIDX;
	}
}
