package org.scrumple.scrumplecore.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
	@XmlElement
	private String handle;
	private String password;
	private Role role;
	
	public User(){}
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
}
