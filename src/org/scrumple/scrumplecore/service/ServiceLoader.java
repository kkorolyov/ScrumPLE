package org.scrumple.scrumplecore.service;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.scrumple.scrumplecore.assets.Assets;
import org.scrumple.scrumplecore.assets.Assets.Config;
import org.scrumple.scrumplecore.auth.Authenticator;
import org.scrumple.scrumplecore.database.Database;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;
import dev.kkorolyov.simpleprops.Properties;

/**
 * Loads all necessary ScrumPLE-Core services based on configuration parameters.
 */
public class ServiceLoader {
	private static final Logger log = Logger.getLogger(ServiceLoader.class.getName(), Level.DEBUG, new PrintWriter(System.err));
	private static Database database;
	private static Authenticator authenticator;
	
	/**
	 * (Re)initializes all services.
	 */
	public static void init() {	// TODO Init for a specific Project schema passed as first param in REST URI
		Assets.init();
		
		try {
			database = new Database("StubProject", new Properties(Config.getFile(Config.SAVEABLES)));	// TODO This is a testing stub
			authenticator = new Authenticator(database);
		} catch (NamingException | SQLException e) {
			log.exception(e);
		}
	}
	
	/** @return currently-loaded database */
	public static Database getDatabase() {
		if (database == null)
			init();
			
		return database;
	}
	/** @return currently-loaded authenticator */
	public static Authenticator getAuthenticator() {
		if (authenticator == null)
			init();
		
		return authenticator;
	}
}
