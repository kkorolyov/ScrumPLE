package org.scrumple.scrumplecore.database;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import org.scrumple.scrumplecore.assets.Assets.LogFiles;
import org.scrumple.scrumplecore.assets.Assets.Sql;
import org.scrumple.scrumplecore.assets.Assets.Strings;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;

public class Database {
	private static final Logger log = Logger.getLogger(Database.class.getName(), Level.DEBUG, new PrintWriter(System.err));
	
	private final Connection conn;
	
	static {
		try {
			log.addWriter(new PrintWriter(LogFiles.get(Database.class)));
		} catch (Exception e) {
			log.severe(Strings.CANNOT_FIND_LOGFILE);
		}
		log.debug("Initialized class");
	}
	
	/**
	 * Constructs a new database connection.
	 * @param url URL to database
	 * @param user user
	 * @param password password
	 * @throws SQLException if a connection error occurs
	 */
	public Database(String url, String user, String password) throws SQLException {
		MysqlDataSource ds = new MysqlDataSource();
		ds.setUrl(url);
		ds.setUser(user);
		ds.setPassword(password);
		
		conn = ds.getConnection();
		conn.setAutoCommit(false);
		
		log.info("Created new Database: URL=" + url + ", user=" + user);
	}

	public Connection getConn(){
		return conn;
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
	
	/**
	 * Initializes the system database.
	 * @return {@code true} if initialization made at least 1 change to the system database
	 */
	public boolean init() {
		boolean result = false;
		
		try {
			int[] updates = executeBatch(SqlReader.read(Sql.getFile(Sql.INIT_DATABASE_SCRIPT)));
			
			if (updates != null) {
				for (int update : updates) {
					if (update > 0) {
						result = true;
						break;
					}
				}
			}
		} catch (FileNotFoundException e) {
			log.exception(e);
		}
		return result;
	}
	
	/**
	 * Convenience method for @link {@link #executeBatch(List)}
	 */
	public int[] executeBatch(String... statements) {
		return executeBatch(Arrays.asList(statements));
	}
	/**
	 * Executes a batch of non-parameterized SQL statements.
	 * @param statements statements to execute
	 * @return array of update counts, or {@code null} if statement execution failed
	 */
	public int[] executeBatch(List<String> statements) {
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
