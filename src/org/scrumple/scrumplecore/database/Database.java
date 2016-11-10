package org.scrumple.scrumplecore.database;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.scrumple.scrumplecore.applications.Project;
import org.scrumple.scrumplecore.applications.User;
import org.scrumple.scrumplecore.assets.Assets.Config;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;
import dev.kkorolyov.simpleprops.Properties;

/**
 * A connection to a single database.
 */
public class Database {
	private static final Logger log = Logger.getLogger(Database.class.getName(), Level.DEBUG, new PrintWriter(System.err));
	private static final String DELIMITER = ",",
															EQUALS = "=",
															AND = " AND ",
															PARAM_MARKER = "?";
	private static final String REFERENCED_TABLE_COLUMN = "PKTABLE_NAME";
	private static final String CREATE_SCHEMA_TEMPLATE = "CREATE SCHEMA IF NOT EXISTS " + PARAM_MARKER,
															DROP_SCHEMA_TEMPLATE = "DROP SCHEMA IF EXISTS " + PARAM_MARKER,
															GET_COLUMNS_TEMPLATE = "SELECT * FROM " + PARAM_MARKER + " LIMIT 1",	// TODO Reduce template redundancy
															INSERT_TEMPLATE = "INSERT INTO " + PARAM_MARKER + " (" + PARAM_MARKER + ") VALUES (" + PARAM_MARKER + ")",
															GET_TEMPLATE = "SELECT * FROM " + PARAM_MARKER + " WHERE id=" + PARAM_MARKER,
															GET_ID_TEMPLATE = "SELECT id FROM " + PARAM_MARKER + " WHERE " + PARAM_MARKER + " LIMIT 1",
															GET_SESSION_ID_TEMPLATE = "SELECT id FROM users WHERE handle=" + PARAM_MARKER + AND + " password=" + PARAM_MARKER + " LIMIT 1";
	
	private final String name;
	private final ColumnFilter columnFilter;
	private final Map<String, String> saveables;
	private final Connection conn;
	
	/**
	 * Creates a new project database.
	 * @param name name of new project
	 * @throws SQLException if a database error occurs
	 */
	public static void createProject(String name) throws SQLException {
		try {
			Database db = new Database("", null);
			db.executeBatch(CREATE_SCHEMA_TEMPLATE.replaceFirst(Pattern.quote(PARAM_MARKER), name));
			db.conn.setCatalog(name);
			
			List<String> initProjectStatements = SqlReader.read(Config.getFile(Config.INIT_DATABASE_SCRIPT), Arrays.asList(name));
			
			db.executeBatch(initProjectStatements);
		} catch (FileNotFoundException | NamingException e) {
			log.exception(e);	// Should not happen
		}
		log.info("Created new database=" + name);
	}
	/**
	 * Drops a project from the database.
	 * @param name name of project to drop
	 * @throws SQLException if a database error occurs
	 */
	public static void dropProject(String name) throws SQLException {
		Database db;
		try {
			db = new Database("", null);
			db.executeBatch(DROP_SCHEMA_TEMPLATE.replaceFirst(Pattern.quote(PARAM_MARKER), name));
		} catch (NamingException e) {
			log.exception(e);	// Should not happen
		}
	}
	
	/**
	 * Constructs a new database with a default column filter.
	 * @see #Database(String, ColumnFilter, Properties)
	 */
	public Database(String databaseName, Properties props) throws NamingException, SQLException {
		this(databaseName, (rsmd, i) -> !rsmd.isAutoIncrement(i), props);
	}
	/**
	 * Constructs a new database connection.
	 * @param databaseName name of database to connect to
	 * @param columnFilter filtering scheme for columns used in ORM functions
	 * @param saveables properties specifying mappings between Java and database types
	 * @throws NamingException if a name lookup error occurs
	 * @throws SQLException if a connection error occurs
	 */
	public Database(String databaseName, ColumnFilter columnFilter, Properties saveables) throws NamingException, SQLException {
		this.name = databaseName;
		this.columnFilter = columnFilter;
		this.saveables = buildSaveables(saveables);
		
		DataSource ds = DataSourcePool.get(this.name);
		
		conn = ds.getConnection();
		conn.setAutoCommit(false);
		
		log.info("Created new Database: " + this.name);
	}
	private static final Map<String, String> buildSaveables(Properties props) {
		Map<String, String> saveables = new HashMap<>();
		
		if (props != null) {
			for (String key : props.keys()) {
				saveables.put(key, props.get(key));
				saveables.put(props.get(key), key);	// Mirror to get both class->table and table->class
			}
		}
		return saveables;
	}
	
	/**
	 * Convenience method for {@link #executeBatch(List)}
	 * @throws SQLException if a database error occurs
	 */
	public int[] executeBatch(String... statements) throws SQLException {
		return executeBatch(Arrays.asList(statements));
	}
	/**
	 * Executes a batch of non-parameterized SQL statements.
	 * @param statements statements to execute
	 * @return array of update counts, or {@code null} if statement execution failed
	 * @throws SQLException if a database error occurs
	 */
	public int[] executeBatch(List<String> statements) throws SQLException {
		int[] result = null;
		
		try (Statement s = conn.createStatement()) {
			for (String statement : statements)
				s.addBatch(statement);
			
			result = s.executeBatch();
			conn.commit();
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
	 * @throws IllegalArgumentException if {@code toSave} is {@code null} or {@code toSave}'s type is not mapped to a database type
	 */
	public long save(Saveable toSave) throws SQLException {
		long result = -1;
		
		if (toSave == null)
			throw new IllegalArgumentException("Cannot save a null object");
		
		String table = dbType(toSave.getClass());	// Ensures that not null
		
		List<Column> columns = getColumns(table);
		List<Object> data = toSave.toData();

		if (columns.size() != data.size())
			throw new IllegalArgumentException("Saveable data does not match corresponding table columns for " + fullName(table) + ": data= " + data.size() + ", columns= " + columns.size());
		
		if ((result = getId(table, columns, data)) < 0) {	// Not yet saved
			save(table, columns, data);
			log.debug("Saved " + fullName(table) + ": " + toSave);
			
			if ((result = getId(table, columns, data)) >= 0)	// Found id of newly-saved object
				log.debug("Found id for newly-saved " + fullName(table) + " '" + toSave + "': " + result);
			else
				log.severe("Failed to locate id for saved " + fullName(table) + ": " + toSave);
		} else
			log.debug("Found id for already-saved " + fullName(table) + " '" + toSave + "': " + result);
		return result;
	}
	private void save(String table, List<Column> columns, List<Object> data) throws SQLException {
		try (PreparedStatement s = conn.prepareStatement(buildInsertBase(table, columns))) {
			for (int i = 0; i < columns.size(); i++)
				s.setObject(i + 1, (data.get(i) instanceof Saveable ? save((Saveable) data.get(i)) : data.get(i)), columns.get(i).type);	// Recursively save Saveable fields
		
			s.executeUpdate();
			conn.commit();
		}
	}
	private long getId(String table, List<Column> columns, List<Object> data) throws SQLException {
		long result = -1;
		
		try (PreparedStatement s = conn.prepareStatement(buildGetIdBase(table, columns))) {
			for (int i = 0; i < columns.size(); i++)
				s.setObject(i + 1, (data.get(i) instanceof Saveable ? save((Saveable) data.get(i)) : data.get(i)), columns.get(i).type);	// TODO Repeat of code in #save() above
			
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
	 * @throws IllegalArgumentException if {@code c} is not mapped to a database type
	 * @throws InstantiationException if {@code c} does not have a nullary constructor or cannot be instantiated for some other reason 
	 * @throws IllegalAccessException if (@code c) or its nullary constructor is inaccessible
	 * @throws IllegalStateException if {@code c} has a foreign key which is not mapped to a {@code Saveable} field
	 */
	public <T extends Saveable> T load(Class<T> c, long id) throws SQLException, InstantiationException, IllegalAccessException {
		T result = null;
		String table = dbType(c);	// Ensures that not null
		
		List<Object> data = load(table, id);
		if (data != null) {
			result = c.newInstance();
			result.fromData(data);

			log.debug("Loaded from " + fullName(table) + " where id=" + id + ": " + result);
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	private List<Object> load(String table, long id) throws SQLException, InstantiationException, IllegalAccessException {
		List<Object> data = null;
		
		try (PreparedStatement s = conn.prepareStatement(GET_TEMPLATE.replaceFirst(Pattern.quote(PARAM_MARKER), table))) {
			s.setLong(1, id);
			ResultSet rs = s.executeQuery();
			
			if (rs.next()) {	// Found saved data
				data = new ArrayList<>();
				ResultSetMetaData rsmd = rs.getMetaData();
				
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (columnFilter.filter(rsmd, i)) {
						String column = rsmd.getColumnName(i);
						Object value = rs.getObject(i);
						
						String referencedTable = getReferencedTable(table, column);	// Check if foreign key
						if (referencedTable != null) {
							try {
								value = load((Class<? extends Saveable>) Class.forName(saveables.get(referencedTable)), rs.getLong(i));
							} catch (ClassNotFoundException e) {
								log.exception(e);
								throw new IllegalStateException(fullName(table) + "." + column + " is not mapped to a Saveable object");
							}
						}
						data.add(value);
					}
				}
			}
		}
		return data;
	}
	private String getReferencedTable(String table, String column) throws SQLException {	// TODO Cache foreign keys per-table, boosts performance?
		try (ResultSet rs = conn.getMetaData().getImportedKeys(null, null, table)) {
			while (rs.next()) {
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					String current = rs.getString(i);
					if (current != null && current.equals(column))
						return rs.getString(REFERENCED_TABLE_COLUMN);
				}
			}
		}
		return null;
	}

	private List<Column> getColumns(String table) throws SQLException {
		List<Column> columns = new ArrayList<>();
		
		try (ResultSet rs = conn.createStatement().executeQuery(GET_COLUMNS_TEMPLATE.replaceFirst(Pattern.quote(PARAM_MARKER), table))) {
			ResultSetMetaData rsmd = rs.getMetaData();
			
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if (columnFilter.filter(rsmd, i))
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
	
	/**
	 * Returns the database ID of a user with specified credentials.
	 * @param handle handle name of user
	 * @param password hashed password of user
	 * @return matched user or {@code null} if no such user
	 * @throws SQLException if a database error occurs
	 */
	public User getUser(String handle, byte[] password) throws SQLException {
		User user = null;
		
		try (PreparedStatement s = conn.prepareStatement(GET_SESSION_ID_TEMPLATE)) {
			s.setString(1, handle);
			s.setString(2, new String(password, "UTF-8"));
			
			ResultSet rs = s.executeQuery();
			if (rs.next())
				user = load(User.class, rs.getLong(1));
		} catch (UnsupportedEncodingException | InstantiationException | IllegalAccessException e) {
			log.exception(e);
		}
		return user;
	}
	
	/*public void save(List<User> toSave) {
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
	}*/
	
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
	
	/*public User load(String toLoad, User u) {
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
		
	}*/
	
	private String dbType(Class<?> c) {
		String dbType = saveables.get(c.getName());
		if (dbType == null)
			throw new IllegalArgumentException("No matching database type for Java type: " + c.getName());
		
		return dbType;
	}
	
	private String fullName(String table) {
		return name + "." + table;
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
