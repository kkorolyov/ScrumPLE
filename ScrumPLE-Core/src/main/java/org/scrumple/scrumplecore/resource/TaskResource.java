package org.scrumple.scrumplecore.resource;

import javax.ws.rs.core.MultivaluedMap;

import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.Task;
import org.scrumple.scrumplecore.auth.UserSession;
import org.scrumple.scrumplecore.scrum.UserStory;

import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * TODO DOCUMENT
 */
public class TaskResource extends CRUDResource<Task> {
	/**
	 * Constructs a new task resource.
	 * @param project project that task belongs to
	 */
	public TaskResource(UserStory story) {
		super(SqlobDAOFactory.getDAOUnderProject(Task.class, story));

		setAuthorizers(Authorizers.onlyUsers(story), SqlobDAOFactory.getDAOUnderProject(UserSession.class, story));
	}

	@Override
	protected Condition parseQuery(MultivaluedMap<String, String> queryParams) {
		String storyId = queryParams.getFirst("storyId");
		if (storyId != null) {
			return new Condition("storyId", "=", +Integer.valueOf(storyId));
		} else return null;
	}
}
