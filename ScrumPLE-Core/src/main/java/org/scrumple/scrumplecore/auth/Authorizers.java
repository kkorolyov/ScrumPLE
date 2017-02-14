package org.scrumple.scrumplecore.auth;

import dev.kkorolyov.sqlob.persistence.Condition;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.User;
import org.scrumple.scrumplecore.database.DAO;

/**
 * A collection of basic {@link Authorizer} implementations.
 */
public class Authorizers {
	/** An authorizer which allows all credentials. */
	public static Authorizer ALL = credentials -> {};
	/** An authorizer which always no credentials. */
	public static Authorizer NONE = credentials -> {throw new AuthorizationException(credentials);};

	/**
	 * Returns an authorizer which allows only users found in a project.
	 * @param project project defining valid users
	 * @return authorizer allowing only users in {@code project}
	 */
	public static Authorizer onlyUsers(Project project) {
		return credentials -> {
			DAO<User> users = SqlobDAOFactory.getDAOUnderProject(User.class, project);
			if (users.get(new Condition("credentials", "=", credentials)).isEmpty()) throw new AuthorizationException(credentials);
		};
	}

	/**
	 * Returns an authorizer which allows only the project owner.
	 * @param project project defining valid owner
	 * @return authorizer allowing only owner of {@code project}
	 */
	public static Authorizer onlyOwner(Project project) {
		DAO<User> users = SqlobDAOFactory.getDAOUnderProject(User.class, project);

		return credentials -> {
			if (users.get(new Condition("credentials", "=", credentials).and("role", "=", "owner")).isEmpty())
				throw new AuthorizationException(credentials);
		};
	}
}
