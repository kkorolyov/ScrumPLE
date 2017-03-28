package org.scrumple.scrumplecore.auth;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends Exception {
	private static final long serialVersionUID = -2947791885840663040L;

	/**
	 * Constructs a new exception with a message mentioning failed authentication.
	 * @param credentials credentials failing authentication
	 */
	public AuthenticationException(Credentials credentials) {
		super("Failed to authenticate " + credentials);
	}
}
