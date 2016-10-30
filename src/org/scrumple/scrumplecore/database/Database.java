package org.scrumple.scrumplecore.database;

import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.scrumple.scrumplecore.applications.Project;
import org.scrumple.scrumplecore.applications.User;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;

/**
 * A connection to a single database.
 */
public class Database {
	private static final Logger log = Logger.getLogger(Database.class.getName(), Level.DEBUG, new PrintWriter(System.err));
	private static final String DELIMITER = ",",
															PARAM_MARKER = "?";
	private static final String CREATE_DATABASE_TEMPLATE = "CREATE DATABASE IF NOT EXISTS " + PARAM_MARKER,
															GET_COLUMNS_TEMPLATE = "SELECT * FROM " + PARAM_MARKER + " LIMIT 1",
															INSERT_TEMPLATE = "INSERT INTO " + PARAM_MARKER + " (" + PARAM_MARKER + ") VALUES (" + PARAM_MARKER + ")";
	
	private final Connection conn;
	private final String name;
	
	/**
	 * Creates a new database.
	 * @param name name of new database
	 */
	public static void createDatabase(String name) {
		try {
			DataSource ds = DataSourcePool.get("");
			ds.getConnection().createStatement().executeUpdate(CREATE_DATABASE_TEMPLATE.replaceFirst(Pattern.quote(PARAM_MARKER), name));
			
			log.info("Created new database=" + name);
		} catch (SQLException e) {
			log.exception(e);
		}
	}
	
	/**
	 * Constructs a new database connection.
	 * @param databaseName name of database to connect to
	 * @throws NamingException if a name lookup error occurs
	 * @throws SQLException if a connection error occurs
	 */
	public Database(String databaseName) throws NamingException, SQLException {
		this.name = databaseName;
		
		DataSource ds = DataSourcePool.get(this.name);
		
		conn = ds.getConnection();
		conn.setAutoCommit(false);
		
		log.info("Created new Database: " + this.name);
	}
	
	/**
	 * Convenience method for {@link #executeBatch(List)}
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
	
	public void createDefaultRoles() {	// TODO Extract to external script?
		try {
			String sql = "INSERT INTO Project.roles (name) VALUES (?)";
			PreparedStatement s = conn.prepareStatement(sql);
			s.setString(1, "Product Owner");
			s.addBatch();
			s.setString(1,  "Scrum Master");
			s.addBatch();
			s.setString(1, "Team Member");
			s.addBatch();
			s.executeBatch();
			conn.commit();

		} catch (SQLException  e) {
			log.exception(e);
			
		}
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
			
			log.debug("Saved to database=" + name + ": " + toSave);
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
	
	public void save(List<User> toSave) {
		String sql = "INSERT INTO Project.users (credentials, name, role) VALUES (?, ?, ?)";

		try {
			PreparedStatement s = conn.prepareStatement(sql);
			for(User user : toSave)
			{
				s.setString(1, user.getCredentials());
				s.setString(2, user.getName());
				s.setInt(3, user.getRole());
				s.addBatch();
			}
			s.executeBatch();
			conn.commit();
		} catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Project load(String toLoad, Project p) {
		String sql = "Select * FROM Project.project WHERE name = ?";
		
		try {
			PreparedStatement s = conn.prepareStatement(sql);
			s.setString(1, toLoad);
			ResultSet rs = s.executeQuery();
			while (rs.next()){
				p.setName(rs.getString("name"));
				p.setDescription(rs.getString("description"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.exception(e);;
		}
		return p;
	}
	
	public User load(String toLoad, User u) {
		String sql = "Select * FROM Project.users WHERE name = ?";
		
		try {
			PreparedStatement s = conn.prepareStatement(sql);
			s.setString(1,  toLoad);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				u.setName(rs.getString("name"));
				u.setCredentials(rs.getString("credentials"));
				u.setRole(rs.getInt("role"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.exception(e);
		}
		return u;
		
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
