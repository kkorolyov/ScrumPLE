package org.scrumple.scrumplecore.database;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.scrumple.scrumplecore.assets.Assets.Config;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;

/**
 * Provides {@code DataSource} objects to various databases.
 * @see DataSource
 */
public class DataSourcePool {
	/** JNDI name prefix for bound {@code DataSource} objects */
	public static final String LOOKUP_PREFIX = "jdbc/";
	
	private static final Logger log = Logger.getLogger(DataSourcePool.class.getName(), Level.DEBUG, (PrintWriter[]) null);
	private static Map<String, DataSource> dataSources = new HashMap<>();
	
	/**
	 * Retrieves a {@code DataSource} to a database
	 * @param databaseName name of database to get {@code DataSource} for
	 * @return appropriate {@code DataSource}
	 */
	public static DataSource get(String databaseName) {
		DataSource ds;
		while ((ds = dataSources.get(databaseName)) == null)
			addDataSource(databaseName);
		
		return ds;
	}
	private static void addDataSource(String databaseName) {
		MysqlDataSource ds = new MysqlDataSource();
		ds.setServerName(Config.getServer());
		ds.setPort(Config.getPort());
		ds.setDatabaseName(databaseName);
		ds.setUser(Config.getUser());
		ds.setPassword(Config.getPassword());
		
		dataSources.put(databaseName, ds);
		
		log.info("Added new DataSource: name=" + databaseName + ", DataSource=" + ds);
	}
}
