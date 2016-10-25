package org.scrumple.scrumplecore.database;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.scrumple.scrumplecore.applications.Project;
import org.scrumple.scrumplecore.assets.Assets.Sql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;

public class Database {
	private static final Logger log = Logger.getLogger(Database.class.getName(), Level.DEBUG, new PrintWriter(System.err));
	
	private final Connection conn;
	
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
	
	/**
	 * Saves an object.
	 * @param toSave object to save
	 * @return {@code true} if {@code toSave} saved successfully
	 */
	public boolean save(Saveable toSave) {
		boolean result = false;
		
		if (toSave == null)
			throw new IllegalArgumentException("Cannot save a null object");
		
		try (PreparedStatement s = buildInsert(toSave)) {
			result = s.executeUpdate() > 0;
			conn.commit();
		} catch (SQLException | IllegalArgumentException e) {
			log.exception(e);
		}
		return result;
	}
	private PreparedStatement buildInsert(Saveable saveable) throws SQLException {
		List<String> saveStatement = Sql.getSaveStatement(saveable.getClass().getSimpleName());
		if (saveStatement == null)
			throw new IllegalArgumentException("Save statement cannot be null");	// TODO Icky
		
		Object[] data = saveable.toData();
		String[] paramTypes = saveStatement.get(0).split(Pattern.quote(","));	// TODO Propertize delimiter
		String statementString = saveStatement.get(1);
		
		PreparedStatement s = conn.prepareStatement(statementString);
		
		for (int i = 0; i < paramTypes.length; i++)
			s.setObject(i + 1, data[i], toTypeCode(paramTypes[i].trim()));

		return s;
	}
	private static final int toTypeCode(String typeName) {
		try {
			return Types.class.getField(typeName).getInt(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new IllegalArgumentException("Invalid type name: " + typeName);
		}
	}
	
	public void save(Project toSave) {
		try {
			String sql = "INSERT INTO Project.project (name, description) VALUES ('"+toSave.getName()+"', '" +toSave.getDescription() + "')";
			PreparedStatement s = conn.prepareStatement(sql);
			s.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.exception(e);;
		}
	}
}
