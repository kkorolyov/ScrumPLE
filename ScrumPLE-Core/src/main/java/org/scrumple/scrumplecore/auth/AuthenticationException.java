package org.scrumple.scrumplecore.auth;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends Exception {
	private static final long serialVersionUID = -2947791885840663040L;

	/**
	 * Constructs a new exception with a {@code null} message.
	 */
	public AuthenticationException() {
		this(null);
	}
	/**
	 * Constructs a new exception with a message mentioning failed authentication for a certain handle.
	 * @param handle user handle failing authentication
	 */
	public AuthenticationException(String handle) {
		super("Failed to authenticate user: " + handle);
	}
}
