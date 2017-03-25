package org.scrumple.scrumplecore.auth;

import org.scrumple.scrumplecore.scrum.Project;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends Exception {
	private static final long serialVersionUID = -2947791885840663040L;

	/**
	 * Constructs a new exception with a message mentioning failed authentication for some project.
	 * @param project project authentication failed for
	 * @param credentials credentials failing authentication
	 */
	public AuthenticationException(Project project, Credentials credentials) {
		super(project + " failed to authenticate " + credentials);
	}
}
