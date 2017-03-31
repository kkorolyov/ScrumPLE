package org.scrumple.scrumplecore.resource;

import javax.ws.rs.core.MultivaluedMap;

import org.scrumple.scrumplecore.auth.Authorizer;
import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.User;
import org.scrumple.scrumplecore.session.UserSession;

import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * Provides endpoints for accessing a project's users.
 */
public class UsersResource extends CRUDResource<User> {
	/**
	 * Constructs a new users resource.
	 * @param project users scope
	 */
	public UsersResource(Project project) {
		super(SqlobDAOFactory.getDAOUnderProject(User.class, project));

		Authorizer onlyOwner = Authorizers.onlyOwner(project);

		setAuthorizers(onlyOwner, Authorizers.ALL, onlyOwner, onlyOwner, SqlobDAOFactory.getDAOUnderProject(UserSession.class, project));
	}

	@Override
	protected Condition parseQuery(MultivaluedMap<String, String> queryParams) {
		return null;
	}
}
