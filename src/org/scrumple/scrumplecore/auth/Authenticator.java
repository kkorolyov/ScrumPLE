package org.scrumple.scrumplecore.auth;

import org.scrumple.scrumplecore.database.Database;

/**
 * Authenticates session credentials.
 */
public class Authenticator {
	private final Database db;
	
	/**
	 * Constructs a new authenticator backed by the specified database.
	 * @param database database providing authentication information
	 */
	public Authenticator(Database database) {
		this.db = database;
	}
	
	/**
	 * Authenticates credentials and creates a new session if authentication successful.
	 * @param credentials credentials to authenticate
	 * @return id of created session, or {@code -1} if an authentication error occurred
	 */
	public long authenticate(String credentials) {
		// TODO
		return -1;
	}
}
