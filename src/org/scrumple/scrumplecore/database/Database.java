package org.scrumple.scrumplecore.database;

import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
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
															EQUALS = "=",
															AND = " AND ",
															OR = " OR ",
															ID = "id",
															PARAM_MARKER = "?";
	private static final String CREATE_DATABASE_TEMPLATE = "CREATE DATABASE IF NOT EXISTS " + PARAM_MARKER,
															GET_COLUMNS_TEMPLATE = "SELECT * FROM " + PARAM_MARKER + " LIMIT 1",
															INSERT_TEMPLATE = "INSERT INTO " + PARAM_MARKER + " (" + PARAM_MARKER + ") VALUES (" + PARAM_MARKER + ")",
															GET_TEMPLATE = "SELECT * FROM " + PARAM_MARKER + " WHERE " + ID + "=" + PARAM_MARKER,
															GET_ID_TEMPLATE = "SELECT " + ID + " FROM " + PARAM_MARKER + " WHERE " + PARAM_MARKER + " LIMIT 1";
	
	private final String name;
	private final ColumnFilter columnFilter;
	private final Connection conn;
	
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
	 * Constructs a new database with a default column filter.
	 * @see #Database(String, ColumnFilter)
	 */
	public Database(String databaseName) throws NamingException, SQLException {
		this(databaseName, (rsmd, i) -> !rsmd.isAutoIncrement(i));
	}
	/**
	 * Constructs a new database connection.
	 * @param databaseName name of database to connect to
	 * @param columnFilter filtering scheme for columns used in ORM functions
	 * @throws NamingException if a name lookup error occurs
	 * @throws SQLException if a connection error occurs
	 */
	public Database(String databaseName, ColumnFilter columnFilter) throws NamingException, SQLException {
		this.name = databaseName;
		this.columnFilter = columnFilter;
		
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
	 * If an equivalent object is already saved, returns the id of the saved object
	 * @param toSave object to save
	 * @return id of saved object, or {@code -1} if failed to save
	 * @throws SQLException if a database error occurs
	 * @throws IllegalArgumentException if {@code toSave} is {@code null}
	 */
	public long save(Saveable toSave) throws SQLException {
		long result = -1;
		
		if (toSave == null)
			throw new IllegalArgumentException("Cannot save a null object");
		
		String table = toSave.getClass().getSimpleName();
		List<Column> columns = getColumns(table);
		List<Object> data = toSave.toData();

		if (columns.size() != data.size())
			throw new IllegalArgumentException("Saveable data does not match corresponding table columns for " + fullName(name, table) + ": data= " + data.size() + ", columns= " + columns.size());
		
		if ((result = getId(table, columns, data)) < 0) {	// Not yet saved
			save(table, columns, data);
			log.debug("Saved " + fullName(name, table) + ": " + toSave);
			
			if ((result = getId(table, columns, data)) >= 0)	// Found id of newly-saved object
				log.debug("Found id for saved " + fullName(name, table) + ": " + result);
			else
				log.severe("Failed to locate id for saved " + fullName(name, table) + ": " + toSave);
		}
		return result;
	}
	private void save(String table, List<Column> columns, List<Object> data) throws SQLException {
		try (PreparedStatement s = conn.prepareStatement(buildInsertBase(table, columns))) {
			for (int i = 0; i < columns.size(); i++)
				s.setObject(i + 1, data.get(i) instanceof Saveable ? save((Saveable) data.get(i)) : data.get(i), columns.get(i).type);
		
			s.executeUpdate();
			conn.commit();
		}
	}
	private long getId(String table, List<Column> columns, List<Object> data) throws SQLException {
		long result = -1;
		
		try (PreparedStatement s = conn.prepareStatement(buildGetIdBase(table, columns))) {
			for (int i = 0; i < columns.size(); i++)
				s.setObject(i + 1, data.get(i), columns.get(i).type);
			
			ResultSet rs = s.executeQuery();
			if (rs.next())
				result = rs.getLong(1);
		}
		return result;
	}
	
	/**
	 * Loads an object
	 * @param c object type
	 * @param id id of instance to load
	 * @return appropriate object, or {@code null} if no such object
	 * @throws SQLException if a database error occurs
	 * @throws InstantiationException if {@code c} does not have a nullary constructor or cannot be instantiated for some other reason 
	 * @throws IllegalAccessException if (@code c) or its nullary constructor is inaccessible
	 */
	public <T extends Saveable> T load(Class<T> c, long id) throws SQLException, InstantiationException, IllegalAccessException {
		T result = null;
		String table = c.getSimpleName();
		
		List<Object> data = load(table, id);
		if (data != null) {
			result = c.newInstance();
			result.fromData(data);

			log.debug("Loaded from " + fullName(name, table) + " with id=" + id + ": " + result);
		}
		return result;
	}
	private List<Object> load(String table, long id) throws SQLException, InstantiationException, IllegalAccessException {
		List<Object> data = null;
		
		try (PreparedStatement s = conn.prepareStatement(buildGetBase(table))) {
			s.setLong(1, id);
			ResultSet rs = s.executeQuery();
			
			if (rs.next()) {	// Found saved data
				data = new ArrayList<>();
				ResultSetMetaData rsmd = rs.getMetaData();
				
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (columnFilter.filter(rsmd, i)) {	// Ignore auto-incrementing columns
						String column = rsmd.getColumnName(i);
						Object value = rs.getObject(i);
						
						if (isForeignKey(table, column)) {
							try {
								value = load((Class<? extends Saveable>) Class.forName(column), (long) value);
							} catch (ClassNotFoundException e) {
								log.severe("This should not happen");
								log.exception(e);
							} catch (ClassCastException e) {
								throw new IllegalArgumentException(column + " field of " + table + " is not Saveable");
							}
						}
						data.add(isForeignKey(table, column) ? load(column, rs.getLong(i)) : rs.getObject(i));	// If foreign key, get referenced object
					}
				}
			}
		}
		return data;
	}
	
	private boolean isForeignKey(String table, String column) throws SQLException {
		ResultSetMetaData rsmd = conn.getMetaData().getExportedKeys(null, null, table).getMetaData();
		
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			if (rsmd.getColumnName(i).equals(column))
				return true;
		}
		return false;
	}
	
	private List<Column> getColumns(String table) throws SQLException {
		List<Column> columns = new ArrayList<>();
		
		try (ResultSet rs = conn.createStatement().executeQuery(GET_COLUMNS_TEMPLATE.replaceFirst(Pattern.quote(PARAM_MARKER), table))) {
			ResultSetMetaData rsmd = rs.getMetaData();
			
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if (columnFilter.filter(rsmd, i))	// Ignore auto-incrementing columns
					columns.add(new Column(rsmd.getColumnName(i), rsmd.getColumnType(i)));
			}
		}
		return columns;
	}
	
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
		
		log.debug("Built INSERT base statement for " + table + ": " + statement);
		
		return statement;
	}
	private static String buildGetBase(String table) {
		String statement = GET_TEMPLATE.replaceFirst(Pattern.quote(PARAM_MARKER), table);
		
		log.debug("Built SELECT statement for + " + table + ": " + statement);
		
		return statement;
	}
	private static String buildGetIdBase(String table, List<Column> columns) {
		StringBuilder whereBuilder = new StringBuilder();
		
		for (int i = 0; i < columns.size(); i++) {
			whereBuilder.append(columns.get(i).name).append(EQUALS).append(PARAM_MARKER);
			
			if ((i + 1) < columns.size())
				whereBuilder.append(AND);
		}
		String	replace = Pattern.quote(PARAM_MARKER),
						statement = GET_ID_TEMPLATE.replaceFirst(replace, table).replaceFirst(replace, whereBuilder.toString());
		
		log.debug("Built SELECT id base statement for " + table + ": " + statement);
		
		return statement;
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
	
	private static final String fullName(String database, String table) {
		return database + "." + table;
	}
	
	/**	Filters columns to use in ORM functions. */
	public static interface ColumnFilter {
		boolean filter(ResultSetMetaData rsmd, int column) throws SQLException;
	}
		
	private class Column {
		final String name;
		final int type;
		
		Column(String name, int type) {
			this.name = name;
			this.type = type;
		}
	}
}
