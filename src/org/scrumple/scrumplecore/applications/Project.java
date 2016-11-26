package org.scrumple.scrumplecore.applications;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import dev.kkorolyov.sqlob.annotation.Transient;

@XmlRootElement
public class Project {
	private String name;
	private String description;
	private boolean isPrivate;
	@Transient
	private List<User> users;
	
	public Project(){}
	public Project(String name, String description, boolean isPrivate) {
		setName(name);
		setDescription(description);
		setPrivate(isPrivate);
		users = new ArrayList<User>();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	/** @return {@code true} if this project is private */
	public boolean isPrivate() {
		return isPrivate;
	}
	/**
	 * Sets this project's {@code private} status.
	 * @param isPrivate whether this project is private
	 */
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	
	public void addUser(User user) {
		users.add(user);
	}
	
	public List<User> getUsers() {
		return users;
	}
	/*public void createProject(){
		try {
			db.getConn().setCatalog("Project");
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		try(Statement sql = db.getConn().createStatement()){
			System.out.println(PROJECT_NAME);
			String name = s.nextLine();
			System.out.println(DESCRIPTION);
			String des = s.nextLine();
			sql.executeUpdate("INSERT INTO Project (name, description) VALUES ('"+name+"', '" +des + "')");
			db.getConn().commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());		}
	}*/

	@Override
	public String toString() {
		return name + ", " + description + ", " + isPrivate;
	}
}
