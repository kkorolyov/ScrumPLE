package ScrumPLEDB;

import java.sql.*;
import java.util.Scanner;

public class Database {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
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
	public static void main(String[] args) {
		Database db = new Database();
		
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
