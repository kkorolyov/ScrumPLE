package org.scrumple.scrumplecore.auth;

import org.scrumple.scrumplecore.database.DAO;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.User;

/**
 * A collection of basic {@link Authorizer} implementations.
 */
public class Authorizers {
	/** An authorizer which allows all credentials. */
	public static Authorizer ALL = credentials -> {};
	/** An authorizer which always no credentials. */
	public static Authorizer NONE = user -> {throw new AuthorizationException(user);};

	/**
	 * Returns an authorizer which allows only users found in a project.
	 * @param project project defining valid users
	 * @return authorizer allowing only users in {@code project}
	 */
	public static Authorizer onlyUsers(Project project) {
		DAO<User> users = SqlobDAOFactory.getDAOUnderProject(User.class, project);

		return user -> 	{
			if (!users.contains(user)) throw new AuthorizationException(user);
		};
	}

	/**
	 * Returns an authorizer which allows only the project owner.
	 * @param project project defining valid owner
	 * @return authorizer allowing only owner of {@code project}
	 */
	public static Authorizer onlyOwner(Project project) {
		DAO<User> users = SqlobDAOFactory.getDAOUnderProject(User.class, project);

		return user -> {
			if (!(user != null && "owner".equals(user.getRole()) && users.contains(user))) throw new AuthorizationException(user);
		};
	}
}
