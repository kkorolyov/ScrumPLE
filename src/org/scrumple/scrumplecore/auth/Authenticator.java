package org.scrumple.scrumplecore.auth;

import java.io.PrintWriter;

import org.scrumple.scrumplecore.applications.User;
import org.scrumple.scrumplecore.database.Database;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;

/**
 * Authenticates session credentials.
 */
public class Authenticator {
	private static final Logger log = Logger.getLogger(Authenticator.class.getName(), Level.DEBUG, new PrintWriter(System.err));
	
	private final Database db;
	
	/**
	 * Constructs a new authenticator backed by the specified database.
	 * @param database database providing authentication information
	 */
	public Authenticator(Database database) {
		this.db = database;
	}
	
	/**
	 * Returns the user backed by {@code credentials}.
	 * @param credentials credentials to use
	 * @return appropriate user
	 * @throws AuthenticationException if an authentication error occurs
	 */
	public User get(String credentials) throws AuthenticationException {
		// TODO
		return null;
	}
}
