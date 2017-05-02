package org.scrumple.scrumplecore.resource;

import javax.ws.rs.core.MultivaluedMap;

import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.Task;
import org.scrumple.scrumplecore.scrum.UserStory;
import org.scrumple.scrumplecore.auth.UserSession;

import dev.kkorolyov.sqlob.utility.Condition;

import java.util.UUID;

/**
 * TODO DOCUMENT
 */
public class TaskResource extends CRUDResource<Task> {
	/**
	 * Constructs a new task resource.
	 * @param project project that task belongs to
	 */
	public TaskResource(UserStory story, Project project) {
		super(SqlobDAOFactory.getDAOUnderProject(Task.class, project));

		setAuthorizers(Authorizers.onlyUsers(project), SqlobDAOFactory.getDAOUnderProject(UserSession.class, project));
	}

	public TaskResource(Project project) {
		super(SqlobDAOFactory.getDAOUnderProject(Task.class, project));

		setAuthorizers(Authorizers.onlyUsers(project), SqlobDAOFactory.getDAOUnderProject(UserSession.class, project));
	}

	@Override
	protected Condition parseQuery(MultivaluedMap<String, String> queryParams) {
		String userIdString = queryParams.getFirst("user");
		if(userIdString != null){
			UUID userId = UUID.fromString(userIdString);
			return new Condition("user", "=", userId);
		} else return null;
	}
}
