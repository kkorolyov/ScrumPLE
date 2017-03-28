package org.scrumple.scrumplecore.auth;

import org.scrumple.scrumplecore.scrum.User;

/**
 * Processes credentials and throws an {@link AuthorizationException} if credentials are unauthorized.
 */
@FunctionalInterface
public interface Authorizer {
	/**
	 * Processes a user and determines whether it is authorized in some context.
	 * @param user user to process
	 * @throws AuthorizationException if {@code user} unauthorized
	 */
	void process(User user);
}
