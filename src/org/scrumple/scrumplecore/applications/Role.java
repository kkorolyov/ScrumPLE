package org.scrumple.scrumplecore.applications;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Statement;

import org.scrumple.scrumplecore.assets.Assets.Sql;
import org.scrumple.scrumplecore.database.Database;
import org.scrumple.scrumplecore.database.SqlReader;


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
			
			db.executeBatch(SqlReader.read(Sql.getFile(Sql.CREATE_ROLES_SCRIPT)));

			db.getConn().commit();
		} 
		catch (SQLException | FileNotFoundException e) {
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
