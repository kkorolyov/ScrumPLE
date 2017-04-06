package org.scrumple.scrumplecore.auth;

/**
 * Exception thrown when an expired session is used.
 */
public class SessionExpiredException extends RuntimeException {
	/**
	 * Constructs a new instance of this exception for an expired session.
	 * @param session expired session
	 */
	public SessionExpiredException(UserSession session) {
		super("Session expired " + (System.currentTimeMillis() - session.getEnd()) + "ms ago");
	}
}
