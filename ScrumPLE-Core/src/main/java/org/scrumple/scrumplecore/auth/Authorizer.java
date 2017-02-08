package org.scrumple.scrumplecore.auth;

/**
 * Provides an interface to process credentials before handling secure actions.
 */
@FunctionalInterface
public interface Authorizer {
	/**
	 * Processes credentials, determining whether they are valid in some context.
	 * @param credentials credentials to process
	 * @throws AuthorizationException if {@code credentials} is unauthorized
	 */
	void process(Credentials credentials) throws AuthorizationException;
}
