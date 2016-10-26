package org.scrumple.scrumplecore.database;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
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
	private static final String DELIMITER = ",",
															PARAM_MARKER = "?",
															GET_COLUMNS_TEMPLATE = "SELECT * FROM " + PARAM_MARKER + " LIMIT 1",
															INSERT_TEMPLATE = "INSERT INTO " + PARAM_MARKER + " (" + PARAM_MARKER + ") VALUES (" + PARAM_MARKER + ")";
	
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
	@SuppressWarnings("synthetic-access")
	private PreparedStatement buildInsert(Saveable saveable) throws SQLException {
		List<Column> columns = getColumns(saveable);
		List<Object> data = saveable.toData();
		
		if (columns.size() != data.size())
			throw new IllegalArgumentException("Saveable data does not match corresponding table columns; data= " + data.size() + ", columns= " + columns.size());
		
		PreparedStatement s = conn.prepareStatement(buildInsertBase(saveable.getClass().getSimpleName(), columns));
		
		for (int i = 0; i < columns.size(); i++)
			s.setObject(i + 1, data.get(i), columns.get(i).type);

		return s;
	}
	@SuppressWarnings("synthetic-access")
	private static String buildInsertBase(String table, List<Column> columns) throws SQLException {
		StringBuilder columnsBuilder = new StringBuilder(),
									valuesBuilder = new StringBuilder();
		
		int counter = 0;
		for (Column column : columns) {
			columnsBuilder.append(column.name);
			valuesBuilder.append(PARAM_MARKER);
			
			if (++counter < columns.size()) {
				columnsBuilder.append(DELIMITER);
				valuesBuilder.append(DELIMITER);
			}
		}
		String 	replace = Pattern.quote(PARAM_MARKER),
						statement = INSERT_TEMPLATE.replaceFirst(replace, table).replaceFirst(replace, columnsBuilder.toString()).replaceFirst(replace, valuesBuilder.toString());
		
		log.debug("Built INSERT base statement: " + statement);
		
		return statement;
	}
	
	@SuppressWarnings("synthetic-access")
	private List<Column> getColumns(Saveable saveable) throws SQLException {
		List<Column> columns = new ArrayList<>();
		
		try (ResultSet rs = conn.createStatement().executeQuery(GET_COLUMNS_TEMPLATE.replaceFirst(Pattern.quote(PARAM_MARKER), saveable.getClass().getSimpleName()))) {
			ResultSetMetaData rsmd = rs.getMetaData();
			
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if (!rsmd.isAutoIncrement(i))	// Ignore auto-incrementing columns
					columns.add(new Column(rsmd.getColumnName(i), rsmd.getColumnType(i)));
			}
		}
		return columns;
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
	
	private class Column {
		private final String name;
		private final int type;
		
		private Column(String name, int type) {
			this.name = name;
			this.type = type;
		}
	}
}
