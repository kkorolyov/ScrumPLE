package org.scrumple.scrumplecore.applications;

import java.util.Arrays;
import java.util.Iterator;
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
	public void setName(String aName) {
		this.name = aName;
	}
	
	public String getCredentials() {
		return this.credentials;
	}
	
	public void setCredentials(String aCred) {
		this.credentials = aCred;
	}
	
	public int getRole() {
		return this.role;
	}
	
	public void setRole(int roleIDX) {
		this.role = roleIDX;
	}

	@Override
	public List<Object> toData() {

		return Arrays.asList(new Object[]{credentials, name, role});

	}

	@Override
	public void fromData(List<Object> data) {
		Iterator<Object> it = data.iterator();
		credentials = (String) it.next();
		name = (String) it.next();
		role = (int) it.next();
		
	}
}
