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
	 * Constructs a new exception with a custom message.
	 * @param message exception message
	 */
	public AuthenticationException(String message) {
		super(message);
	}
}
