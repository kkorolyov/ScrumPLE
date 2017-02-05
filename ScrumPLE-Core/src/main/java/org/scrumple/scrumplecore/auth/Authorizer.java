package org.scrumple.scrumplecore.auth;

/**
 * Authorizes credentials to invoke REST methods.
 */
public interface Authorizer {
	/** @return {@code true} if {@code credentials} can invoke GET */
	boolean canGET(Credentials credentials);

	/** @return {@code true} if {@code credentials} can invoke POST */
	boolean canPOST(Credentials credentials);

	/** @return {@code true} if {@code credentials} can invoke PUT */
	boolean canPUT(Credentials credentials);

	/** @return {@code true} if {@code credentials} can invoke DELETE */
	boolean canDELETE(Credentials credentials);
}
