package org.scrumple.scrumplecore.resource;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.UUID;

import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.UserStory;
import org.scrumple.scrumplecore.auth.UserSession;

import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * TODO Document
 */
public class UserStoryResource extends CRUDResource<UserStory> {
	Project project;
	/**
	 * Constructs a new user story resource.
	 * @param project project that the user story belongs to
	 */
	public UserStoryResource(Project project) {
		super(SqlobDAOFactory.getDAOUnderProject(UserStory.class, project));

		setAuthorizers(Authorizers.onlyUsers(project), SqlobDAOFactory.getDAOUnderProject(UserSession.class, project));
		this.project = project;
	}

	@Override
	protected Condition parseQuery(MultivaluedMap<String, String> queryParams) {
		String sprintNumber = queryParams.getFirst("sprintNumber");
		if (sprintNumber != null) {
			return new Condition("sprintNumber", "=", Integer.valueOf(sprintNumber));
		} else return null;
	}

	@Path("{uuid}/tasks")
	public TaskResource getTasks(@PathParam("uuid") UUID id) {
		return new TaskResource(retrieve(id, null), project);
	}
}
