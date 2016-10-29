package org.scrumple.scrumplecore.applications;

import java.util.Arrays;
import java.util.List;

import org.scrumple.scrumplecore.database.Saveable;

public class User implements Saveable {
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

	@Override
	public List<Object> toData() {

		return Arrays.asList(new Object[]{credentials, name, role});

	}
}
