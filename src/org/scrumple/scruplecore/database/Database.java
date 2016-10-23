package org.scrumple.scruplecore.database;

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
			s.addBatch("CREATE TABLE IF NOT EXISTS Project " + "(name VARCHAR(64), description VARCHAR(256), PRIMARY KEY (name))");
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
	public static void main(String[] args) {
		Database db = new Database();
		Assets.init();
		db.registerJDBC();
		db.connect();
		db.createDB("System");
		db.createDB("Project");
		db.createProjectSchema();
		try {
			db.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
