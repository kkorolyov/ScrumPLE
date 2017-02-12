package org.scrumple.scrumplecore.scrum;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.scrumple.scrumplecore.auth.Credentials;

public class User {
	@JsonProperty
	private Credentials credentials;
	@JsonIgnore
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
