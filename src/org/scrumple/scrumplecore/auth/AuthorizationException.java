package org.scrumple.scrumplecore.auth;

/**
 * Exception thrown when attempting to execute an unauthorized request.
 */
public class AuthorizationException extends Exception {
	private static final long serialVersionUID = -6594406721203830328L;
	
	/**
	 * Constructs a new exception with a {@code null} message.
	 */
	public AuthorizationException() {
		this(null);
	}
	/**
	 * Constructs a new exception with a custom message.
	 * @param message exception message
	 */
	public AuthorizationException(String message) {
		super(message);
	}
}
