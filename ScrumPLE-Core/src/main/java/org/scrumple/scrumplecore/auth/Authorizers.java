package org.scrumple.scrumplecore.auth;

import dev.kkorolyov.sqlob.persistence.Condition;
import org.scrumple.scrumplecore.scrum.User;
import org.scrumple.scrumplecore.database.DAO;

/**
 * A collection of basic {@link Authorizer} implementations.
 */
public class Authorizers {
	/** An authorizer which does no credentials processing. */
	public static Authorizer NONE = credentials -> {};
	/** An authorizer which always throws an {@code AuthorizationException}. */
	public static Authorizer FORBIDDEN = credentials -> {throw new AuthorizationException(credentials);};

	/**
	 * Constructs an authorizer which throws an {@code AuthorizationException} if credentials are not found in a collection of users.
	 * @param users user collection defining valid credentials
	 * @return authorizer allowing only credentials matching {@code users}
	 */
	public static Authorizer onlyUsersInDAO(DAO<User> users) {
		return credentials -> {
			if (users.get(new Condition("credentials", "=", credentials)).isEmpty())
				throw new AuthorizationException(credentials);
		};
	}
}
