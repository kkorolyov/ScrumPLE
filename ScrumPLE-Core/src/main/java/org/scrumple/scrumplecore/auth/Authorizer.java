package org.scrumple.scrumplecore.auth;

/**
 * Processes credentials and throws an {@link AuthorizationException} if credentials are unauthorized.
 */
@FunctionalInterface
public interface Authorizer {
	/**
	 * Processes credentials, determining whether they are valid in some context.
	 * @param credentials credentials to process
	 * @throws AuthorizationException if {@code credentials} unauthorized
	 */
	void process(Credentials credentials);
}
