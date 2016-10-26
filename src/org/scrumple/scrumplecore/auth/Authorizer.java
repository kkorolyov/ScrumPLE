package org.scrumple.scrumplecore.auth;

import org.scrumple.scrumplecore.database.Database;
import org.scrumple.scrumplecore.session.Session;
import org.scrumple.scrumplecore.session.action.Action;

/**
 * Authorizes session actions.
 */
public class Authorizer {
	private final Database db;
	
	/**
	 * Constructs a new authorizer backed by the specified database.
	 * @param database database providing authorization information
	 */
	public Authorizer(Database database) {
		this.db = database;
	}
	
	/** @return {@code true} if {@code session} is authorized to perform {@code action} */
	boolean isAuthorized(Session session, Action action) {
		// TODO
		return false;
	}
}
