package org.scrumple.scrumplecore.session;

import org.scrumple.scrumplecore.database.Database;

/**
 * Manages a collection of {@code Session} objects.
 */
public class SessionManager {
	private final Database db;
	
	/**
	 * Constructs a new session manager backed by the specified database.
	 * @param database database providing session information
	 */
	public SessionManager(Database database) {
		this.db = database;
	}
	
	boolean createSession(long sessionId) {
		// TODO Add session to DB
		return false;
	}
	
	/**
	 * Terminates a session.
	 * @param sessionId id of session to terminate
	 * @return {@code true} if the session was found and terminated
	 */
	boolean terminateSession(long sessionId) {
		// TODO Update DB with session='inactive'
		return false;
	}
	
	/** @return session matching {@code sessionId}, or {@code null} if no such session */
	public Session getSession(long sessionId) {
		// TODO Get session from DB
		return null;
	}
	
	boolean isValid(long sessionId) {
		// TODO Check session='active' in DB
		return false;
	}
}
