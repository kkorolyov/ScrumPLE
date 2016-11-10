package org.scrumple.scrumplecore.applications;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.scrumple.scrumplecore.database.Saveable;

public class User implements Saveable {
	private String handle;
	private String password;
	private Role role;
	
	public User(String handle, String password, Role role) {
		this.handle = handle;
		this.password = password;
		this.role = role;
	}
	
	public String getHandle() {
		return this.handle;
	}
	public void setName(String handle) {
		this.handle = handle;
	}
		
	public void setPassword(String password) {
		// TODO Hash before set?
		this.password = password;
	}
	
	public Role getRole() {
		return this.role;
	}
	
	public void setRole(Role roleIDX) {
		this.role = roleIDX;
	}

	@Override
	public List<Object> toData() {

		return Arrays.asList(handle, password, role);

	}

	@Override
	public void fromData(List<Object> data) {
		Iterator<Object> it = data.iterator();
		handle = (String) it.next();
		password = (String) it.next();
		role = (Role) it.next();
		
	}
}
