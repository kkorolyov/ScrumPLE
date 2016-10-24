package org.scrumple.scrumplecore.applications;

import java.sql.SQLException;
import java.sql.Statement;

import org.scrumple.scrumplecore.database.Database;


public class Role {

	Database db;
	public Role(String host, String port, String user, String password) throws SQLException {
		db = new Database(host, port, user, password);

	}
	
	public void createDefaultRoles(){
		try {
			db.getConn().setCatalog("Project");
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		try(Statement sql = db.getConn().createStatement()){

			sql.executeUpdate("INSERT INTO Roles (name) VALUES ('Product Owner')");
			sql.executeUpdate("INSERT INTO Roles (name) VALUES ('Scrum Master')");
			sql.executeUpdate("INSERT INTO Roles (name) VALUES ('Team Member')");
			db.getConn().commit();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());		
		}
	}
	
	public void addRole(String name) {
		try {
			db.getConn().setCatalog("Project");
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		try(Statement sql = db.getConn().createStatement()){

			sql.executeUpdate("INSERT INTO Roles (name) VALUES ('"+name+"')");
	
			db.getConn().commit();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());		
		}
	}

}
