package org.scrumple.scrumplecore.auth;

import org.scrumple.scrumplecore.scrum.User;

/**
 * Exception thrown when attempting to execute an unauthorized request.
 */
public class AuthorizationException extends RuntimeException {
	private static final long serialVersionUID = -6594406721203830328L;
	
	/**
	 * Constructs a new exception with a message referencing failed authorization for a certain user.
	 * @param user user failing authorization
	 */
	public AuthorizationException(User user) {
		super(user + " not authorized");
	}
	/**
	 * Constructs a new exception with a message reference failed authorization for a credentials-action pair.
	 * @param user user failing authorization
	 * @param action action unauthorized for {@code user}
	 */
	public AuthorizationException(User user, String action) {
		super(user + " not authorized for " + action);
	}
}
