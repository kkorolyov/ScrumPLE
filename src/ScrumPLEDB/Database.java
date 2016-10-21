package ScrumPLEDB;

import java.sql.*;
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
			//Scanner sc = new Scanner(System.in);
			System.out.print("password:");
			password = sc.next();
			
			conn = DriverManager.getConnection("jdbc:mysql://localhost/?user=root&password="+password);
		} catch (SQLException e) {
			System.out.println("Incorrect password");
			System.exit(1);
		}
	}

	void createDB(String dbName) {
		try {
			//Scanner sc2 = new Scanner(System.in);
			//System.out.print("Database Name:");
			//dbName = sc.next();
			//sc2.close();
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
			conn.setCatalog("Project");
			Statement s = conn.createStatement();
			s.execute("CREATE TABLE IF NOT EXISTS Project " + "(name VARCHAR(64), description VARCHAR(256), PRIMARY KEY (name))");
			s.execute("CREATE TABLE IF NOT EXISTS Roles (id INT, name VARCHAR(64), PRIMARY KEY (id))");
			s.execute("CREATE TABLE IF NOT EXISTS Users (id INT, credentials VARCHAR(128), name VARCHAR(64), role INT, PRIMARY KEY (id),FOREIGN KEY (role) REFERENCES Roles (id))");
			s.close();
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	public static void main(String[] args) {
		Database db = new Database();
		
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
