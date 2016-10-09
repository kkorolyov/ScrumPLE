package implementationSample;

import java.sql.*;
import java.util.Scanner;

public class SampleDatabase {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/EMP";
	String password;
	String dbName;
	Connection conn;
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
			Scanner sc = new Scanner(System.in);
			System.out.print("password:");
			password = sc.next();
			
			conn = DriverManager.getConnection("jdbc:mysql://localhost/?user=root&password="+password);
		} catch (SQLException e) {
			System.out.println("Incorrect password");
			System.exit(1);
		}
	}
	
	void createDB() {
		try {
			Scanner sc2 = new Scanner(System.in);
			System.out.print("Database Name:");
			dbName = sc2.next();
			sc2.close();
			Statement s = conn.createStatement();
			s.execute("CREATE DATABASE " + dbName);
			s.close();
		} catch (SQLException e) {
			System.out.println("Could not create database. Database might already exist");
			System.exit(1);
		}
		
	}
	public static void main(String[] args){
		SampleDatabase db = new SampleDatabase();
		
		db.registerJDBC();
		db.connect();
		db.createDB();
		try {
			db.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

