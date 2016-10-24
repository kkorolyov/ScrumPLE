package org.scrumple.scrumplecore.applications;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Role {
	
	private Scanner s = new Scanner(System.in);
	private final Connection conn;
	
	public Role(String host, String port, String user, String password) throws SQLException {
		MysqlDataSource ds = new MysqlDataSource();
		ds.setServerName(host);
		ds.setPort(Integer.parseInt(port));
		ds.setUser(user);
		ds.setPassword(password);
		
		conn = ds.getConnection();
		conn.setAutoCommit(false);
	}
	
	public void createDefaultRoles(){
		try {
			conn.setCatalog("Project");
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		try(Statement sql = conn.createStatement()){

			sql.executeUpdate("INSERT INTO Roles (name) VALUES ('Product Owner')");
			sql.executeUpdate("INSERT INTO Roles (name) VALUES ('Scrum Master')");
			sql.executeUpdate("INSERT INTO Roles (name) VALUES ('Team Member')");
			conn.commit();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());		
		}
	}
	
	public void addRole(String name) {
		try {
			conn.setCatalog("Project");
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		try(Statement sql = conn.createStatement()){

			sql.executeUpdate("INSERT INTO Roles (name) VALUES ('"+name+"')");
	
			conn.commit();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());		
		}
	}

}
