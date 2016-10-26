package org.scrumple.scrumplecore.applications;

import java.util.ArrayList;
import java.util.List;

public class Project {
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
	
	public String getDescription() {
		return this.projDescription;
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


}
