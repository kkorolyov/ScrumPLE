package org.scrumple.scrumplecore.applications;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.scrumple.scrumplecore.database.Saveable;

import dev.kkorolyov.sqlob.annotation.Transient;

@XmlRootElement
public class Project implements Saveable {
	private String name;
	private String description;
	@Transient
	private List<User> users;
	
	public Project(){}
	public Project(String name, String description) {
		this.name = name;
		this.description = description;
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
	public List<Object> toData() {
		// TODO Auto-generated method stub
		return Arrays.asList(new Object[]{name, description});
	}

	@Override
	public void fromData(List<Object> data) {
		Iterator<Object> it = data.iterator();
		//How to handle loading a list of users?
		name = (String) it.next();
		description = (String) it.next();
		
	}


}
