package org.scrumple.scrumplecore.applications;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.scrumple.scrumplecore.database.Saveable;

public class Project implements Saveable {
	private String projectName;
	private String projDescription;
	private List<User> users;
	
	public Project(String name, String description) {
		this.projectName = name;
		this.projDescription = description;
		users = new ArrayList<User>();

	}
	
	public String getName() {
		return this.projectName;
	}
	
	public void setName(String name) {
		this.projectName = name;
	}
	
	public String getDescription() {
		return this.projDescription;
	}
	
	public void setDescription(String description) {
		this.projDescription = description;
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
		return Arrays.asList(new Object[]{getName(), getDescription()});
	}


}
