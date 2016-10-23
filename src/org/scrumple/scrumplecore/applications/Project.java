package org.scrumple.scrumplecore.applications;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Project {
	private Scanner s = new Scanner(System.in);
	private final Connection conn;
	private static final String PROJECT_NAME = "Project Name: ";
	private static final String DESCRIPTION = "Project Description";
	
	public Project(String host, String port, String user, String password) throws SQLException {
		MysqlDataSource ds = new MysqlDataSource();
		ds.setServerName(host);
		ds.setPort(Integer.parseInt(port));
		ds.setUser(user);
		ds.setPassword(password);
		
		conn = ds.getConnection();
		conn.setAutoCommit(false);

	}
	
	public void createProject(){
		try {
			conn.setCatalog("Project");
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		try(Statement sql = conn.createStatement()){
			System.out.println(PROJECT_NAME);
			String name = s.nextLine();
			System.out.println(DESCRIPTION);
			String des = s.nextLine();
			sql.executeUpdate("INSERT INTO Project (name, description) VALUES ('"+name+"', '" +des + "')");
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());		}
	}


}
