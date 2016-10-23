package org.scrumple.scrumplecore.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.scrumple.scrumplecore.assets.Assets;
import org.scrumple.scrumplecore.assets.Assets.Sql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;

public class Database {
	private static final Logger log = Logger.getLogger(Database.class.getName(), Level.DEBUG, new PrintWriter(System.err));
	
	private final Connection conn;
	
	static {
		try {
			log.addWriter(new PrintWriter(Assets.LogFiles.get(Database.class)));
		} catch (Exception e) {
			log.severe("Unable to locate log file for this class");
		}
		log.debug("Initialized class");
	}
	
	/**
	 * Constructs a new database connection.
	 * @param host host address
	 * @param port database service port
	 * @param user user
	 * @param password password
	 * @throws SQLException if a connection error occurs
	 */
	public Database(String host, String port, String user, String password) throws SQLException {
		MysqlDataSource ds = new MysqlDataSource();
		ds.setServerName(host);
		ds.setPort(Integer.parseInt(port));
		ds.setUser(user);
		ds.setPassword(password);
		
		conn = ds.getConnection();
		conn.setAutoCommit(false);
		
		log.info(	"Created new Database: " + System.lineSeparator()
						+ "\tHost: " + host + System.lineSeparator()
						+ "\tPort: " + port + System.lineSeparator()
						+ "\tUser: " + user);
	}

	void createDB(String dbName) {
		try (Statement s = conn.createStatement()) {
			s.execute("CREATE DATABASE IF NOT EXISTS " + dbName);
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
	
	public int[] createProjectSchema() {
		try {
			conn.setCatalog("Project");
		} catch (SQLException e) {
			log.exception(e);
			
			return null;
		}
		return executeBatch("CREATE TABLE IF NOT EXISTS Project " + "(name VARCHAR(64), description VARCHAR(256), PRIMARY KEY (name))",
												"CREATE TABLE IF NOT EXISTS Roles (id INT UNSIGNED AUTO_INCREMENT, name VARCHAR(64) NOT NULL , PRIMARY KEY (id))",
												"CREATE TABLE IF NOT EXISTS Users (id INT UNSIGNED AUTO_INCREMENT, credentials VARCHAR(128) NOT NULL, name VARCHAR(64) NOT NULL, role INT UNSIGNED NOT NULL, PRIMARY KEY (id),FOREIGN KEY (role) REFERENCES Roles (id))",
												"CREATE TABLE IF NOT EXISTS Labels (id INT UNSIGNED AUTO_INCREMENT, name VARCHAR(64) NOT NULL, PRIMARY KEY (id))",
												"CREATE TABLE IF NOT EXISTS Tasks (id INT UNSIGNED AUTO_INCREMENT, label INT UNSIGNED NOT NULL, description VARCHAR(256) NOT NULL, hours_left TINYINT NOT NULL, `release` INT, sprint INT, PRIMARY KEY (id), FOREIGN KEY (label) REFERENCES Labels (id))",
												Assets.Sql.get(Sql.RELEASES));
	}
	
	/**
	 * Executes a batch of non-parameterized SQL statements.
	 * @param statements statements to execute
	 * @return array of update counts, or {@code null} if statement execution failed
	 */
	public int[] executeBatch(String... statements) {
		int[] result = null;
		
		try (Statement s = conn.createStatement()) {
			for (String statement : statements)
				s.addBatch(statement);
			
			result = s.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			log.exception(e);
		}
		return result;
	}
}
