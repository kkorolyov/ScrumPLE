package org.scrumple.scrumplecore.resource;

import javax.ws.rs.core.MultivaluedMap;

import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.UserStory;
import org.scrumple.scrumplecore.session.UserSession;

import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * TODO Document
 */
public class UserStoryResource extends CRUDResource<UserStory> {
	/**
	 * Constructs a new user story resource.
	 * @param project project that the user story belongs to
	 */
	public UserStoryResource(Project project) {
		super(SqlobDAOFactory.getDAOUnderProject(UserStory.class, project));

		setAuthorizers(Authorizers.onlyUsers(project), SqlobDAOFactory.getDAOUnderProject(UserSession.class, project));
	}

	@Override
	protected Condition parseQuery(MultivaluedMap<String, String> queryParams) {
		String sprintNumber = queryParams.getFirst("sprintNumber");
		if (sprintNumber != null) {
			return new Condition("sprintNumber", "=", Integer.valueOf(sprintNumber));
		} else return null;
	}
}
