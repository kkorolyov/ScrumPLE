package org.scrumple.scrumplecore.database;

import static org.scrumple.scrumplecore.assets.Assets.*;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.scrumple.scrumplecore.assets.Assets;

import com.mysql.cj.jdbc.MysqlDataSource;

import dev.kkorolyov.simplelogs.Level;
import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.format.Formatters;

/**
 * Provides {@code DataSource} objects to various databases.
 * @see DataSource
 */
public class DataSourcePool {
	private static final Logger log = Logger.getLogger(Level.DEBUG, Formatters.simple());

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
		ds.setUrl("jdbc:mysql://" +
							Assets.get(DB_HOST) +
							":" + Assets.get(DB_PORT) +
							"/" + databaseName +
							"?useLegacyDatetimeCode=false&serverTimezone=America/Los_Angeles");

		ds.setUser(Assets.get(DB_USER));
		ds.setPassword(Assets.get(DB_PASSWORD));

		dataSources.put(databaseName, ds);

		log.info("Added new DataSource: name=" + databaseName + ", DataSource=" + ds);
	}
}
