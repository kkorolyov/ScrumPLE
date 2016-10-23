package org.scrumple.scrumplecore.database;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

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
			conn.setCatalog(Sql.get(Sql.PROJECT_SCHEMA));
			
			return executeBatch(SqlReader.read(Sql.getFile(Sql.PROJECT_SCHEMA_SCRIPT)));
		} catch (SQLException | FileNotFoundException e) {
			log.exception(e);
		}
		/*return executeBatch(Assets.Sql.get(Sql.PROJECT),
							Assets.Sql.get(Sql.ROLES),
							Assets.Sql.get(Sql.USERS),
							Assets.Sql.get(Sql.SPRINTS),
							Assets.Sql.get(Sql.LABELS),
							Assets.Sql.get(Sql.RELEASES),
							Assets.Sql.get(Sql.TASKS),
							Assets.Sql.get(Sql.ISSUES));*/
		return null;
	}
	
	public int[] createSystemSchema() {
		try {
			conn.setCatalog(Sql.get(Sql.SYSTEM_SCHEMA));
			
			return executeBatch(SqlReader.read(Sql.getFile(Sql.SYSTEM_SCHEMA_SCRIPT)));
		} catch (SQLException | FileNotFoundException e) {
			log.exception(e);
		}
		/*return executeBatch(Assets.Sql.get(Sql.EVENT_CODES),
							Assets.Sql.get(Sql.PROJECTS),
							Assets.Sql.get(Sql.SESSIONS),
							Assets.Sql.get(Sql.EVENTS));*/
		return null;
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