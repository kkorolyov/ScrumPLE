package org.scrumple.scrumplecore.auth;

/**
 * Exception thrown when attempting to execute an unauthorized request.
 */
public class AuthorizationException extends RuntimeException {
	private static final long serialVersionUID = -6594406721203830328L;
	
	/**
	 * Constructs a new exception with a {@code null} message.
	 */
	public AuthorizationException() {
		this(null);
	}
	/**
	 * Constructs a new exception with a message referencing failed authorization for certain credentials.
	 * @param credentials credentials failing authorization
	 */
	public AuthorizationException(Credentials credentials) {
		super("Credentials not authorized: " + credentials);
	}
	/**
	 * Constructs a new exception with a message reference failed authorization for a credentials-action pair.
	 * @param credentials credentials failing authorization
	 * @param action action unauthorized for {@code credentials}
	 */
	public AuthorizationException(Credentials credentials, String action) {
		super("Credentials not authorized for " + action + ": " + credentials);
	}
}
