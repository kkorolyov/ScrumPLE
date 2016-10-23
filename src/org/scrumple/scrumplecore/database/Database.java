package org.scrumple.scrumplecore.database;

import java.sql.*;
import org.scrumple.scrumplecore.assets.Assets;
import org.scrumple.scrumplecore.assets.Assets.Sql;

import com.mysql.jdbc.jdbc2.optional.*;
import java.util.Scanner;

public class Database {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	String password;
	String dbName;
	Connection conn;
	Scanner sc = new Scanner(System.in);
	void registerJDBC() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch(ClassNotFoundException ex) {
			System.out.println("Error: unable to load driver class!");
			System.exit(1);
		}
	}
	
	void connect() {
		try {
			com.mysql.jdbc.jdbc2.optional.MysqlDataSource ds = new com.mysql.jdbc.jdbc2.optional.MysqlDataSource();
			ds.setServerName("localhost");
			ds.setPortNumber(3306);
			//ds.setDatabaseName("Project");
			ds.setUser("root");
			System.out.print("password:");
			password = sc.next();
			ds.setPassword(password);
			conn = ds.getConnection();
			
		} catch (SQLException e) {
			System.out.println("Incorrect password");
			System.exit(1);
		}
	}

	void createDB(String dbName) {
		try {
			Statement s = conn.createStatement();
			s.execute("CREATE DATABASE IF NOT EXISTS " + dbName);
			s.close();
		} 
		catch (SQLException e) {
		    if (e.getErrorCode() == 1007) {
		        // Database already exists error
		        System.out.println(e.getMessage());
		    } else {
		        // Some other problems, e.g. Server down, no permission, etc
		        e.printStackTrace();
		    }
		}
	}
	
	void createProjectSchema() {
		try{
			conn.setAutoCommit(false);
			conn.setCatalog("Project");
			
			Statement s = conn.createStatement();
			s.addBatch(Assets.Sql.get(Sql.PROJECT));
			s.addBatch("CREATE TABLE IF NOT EXISTS Roles (id INT UNSIGNED AUTO_INCREMENT, name VARCHAR(64) NOT NULL , PRIMARY KEY (id))");
			s.addBatch("CREATE TABLE IF NOT EXISTS Users (id INT UNSIGNED AUTO_INCREMENT, credentials VARCHAR(128) NOT NULL, name VARCHAR(64) NOT NULL, role INT UNSIGNED NOT NULL, PRIMARY KEY (id),FOREIGN KEY (role) REFERENCES Roles (id))");
			s.addBatch("CREATE TABLE IF NOT EXISTS Labels (id INT UNSIGNED AUTO_INCREMENT, name VARCHAR(64) NOT NULL, PRIMARY KEY (id))");
			s.addBatch("CREATE TABLE IF NOT EXISTS Tasks (id INT UNSIGNED AUTO_INCREMENT, label INT UNSIGNED NOT NULL, description VARCHAR(256) NOT NULL, hours_left TINYINT NOT NULL, `release` INT, sprint INT, PRIMARY KEY (id), FOREIGN KEY (label) REFERENCES Labels (id))");
			s.addBatch(Assets.Sql.get(Sql.RELEASES));
			int[] count = s.executeBatch();
			conn.commit();
			
			s.close();
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	void createSystemSchema(){
		try{
			conn.setAutoCommit(false);
			conn.setCatalog("Project");
			
			Statement s =conn.createStatement();
			s.addBatch(Assets.Sql.get(Sql.SYSTEM));
			s.addBatch("CREATE TABLE IF NOT EXISTS Event_codes (id INT UNSIGNED AUTO_INCREMENT, name VARCHAR(128) NOT NULL, PRIMARY KEY (id))");
			s.addBatch("CREATE TABLE IF NOT EXISTS Projects (id INT UNSIGNED AUTO_INCREMENT, schema VARCHAR(64) NOT NULL, PRIMARY KEY (id))");
			s.addBatch("CREATE TABLE IF NOT EXISTS Sessions (id BIGINT UNSIGNED AUTO_INCREMENT, source_4 CHAR(8) NULL, source_6 CHAR(32) NULL, start TIMESTAMP NOT NULL, project INT UNSIGNED NOT NULL, user INT UNSIGNED NOT NULL, active BIT(1) NOT NULL, PRIMARY KEY (id), INDEX `project_idx` (`project` ASC), INDEX `user_idx` (`user` ASC), FOREIGN KEY (project) REFERENCES Projects (id) ON DELETE NO ACTION ON UPDATE NO ACTION, FOREIGN KEY (user) REFERENCES Users (id) ON DELETE NO ACTION ON UPDATE NO ACTION)");
			s.addBatch("CREATE TABLE IF NOT EXISTS Events (id BIGINT UNSIGNED AUTO_INCREMENT, time TIMESTAMP NOT NULL, event_code INT UNSIGNED NOT NULL, description VARCHAR(256) NULL, session BIGINT UNSIGNED NOT NULL, INDEX `event_idx` (`event_code` ASC), PRIMARY KEY (id), INDEX `session_idx` (`session` ASC), FOREIGN KEY (Event_code) REFERENCES Event_codes (id) ON DELETE NO ACTION ON UPDATE NO ACTION, FOREIGN KEY (Session) REFERENCES Sessions (id) ON DELETE NO ACTION ON UPDATE NO ACTION)");
			int[] count = s.executeBatch();
			conn.commit();
			
			s.close();
			
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
	}
	public static void main(String[] args) {
		Database db = new Database();
		Assets.init();
		db.registerJDBC();
		db.connect();
		db.createDB("System");
		db.createDB("Project");
		db.createProjectSchema();
		db.createSystemSchema();
		try {
			db.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
