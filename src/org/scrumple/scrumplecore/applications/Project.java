package org.scrumple.scrumplecore.applications;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import org.scrumple.scrumplecore.database.Database;

public class Project {
	private Scanner s = new Scanner(System.in);

	private static final String PROJECT_NAME = "Project Name: ";
	private static final String DESCRIPTION = "Project Description: ";
	private Database db;
	
	public Project(String host, String port, String user, String password) throws SQLException {
		db = new Database(host, port, user, password);

	}
	
	public void createProject(){
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
	}


}
