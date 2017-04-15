package org.scrumple.scrumplecore.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;

import org.scrumple.scrumplecore.auth.Authorizer;
import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.User;
import org.scrumple.scrumplecore.auth.UserSession;

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

		Authorizer onlyUsers = Authorizers.onlyUsers(project);

		setAuthorizers(Authorizers.ALL, Authorizers.ALL, onlyUsers, onlyUsers, SqlobDAOFactory.getDAOUnderProject(UserSession.class, project));
	}

	@Path("invite")
	@POST
	public void invite(String email, String message, String url) {

	}

	@Override
	protected Condition parseQuery(MultivaluedMap<String, String> queryParams) {
		String displayName = queryParams.getFirst("displayName");

		return (displayName == null) ? null : new Condition("displayName" , "LIKE", "%" + displayName + "%");
	}
}
