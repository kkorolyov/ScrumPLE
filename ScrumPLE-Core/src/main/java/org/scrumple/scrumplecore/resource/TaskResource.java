package org.scrumple.scrumplecore.resource;

import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.auth.UserSession;
import org.scrumple.scrumplecore.database.DAO;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.mail.Assignment;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.Task;
import org.scrumple.scrumplecore.scrum.User;
import org.scrumple.scrumplecore.scrum.UserStory;

import dev.kkorolyov.sqlob.utility.Condition;

/**
 * TODO DOCUMENT
 */
public class TaskResource extends CRUDResource<Task> {
	private final Project project;

	/**
	 * Constructs a new task resource.
	 * @param project project that task belongs to
	 */
	public TaskResource(Project project) {
		this(null, project);
	}
	/**
	 * Constructs a new task resource.
	 * @param project project that task belongs to
	 */
	public TaskResource(UserStory story, Project project) {
		super(SqlobDAOFactory.getDAOUnderProject(Task.class, project));
		this.project = project;

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

	@Override
	public void update(UUID id, Task replacement, HttpHeaders headers) {
		super.update(id, replacement, headers);

		if (replacement.getUser() != null) {
			DAO<User> userDAO = SqlobDAOFactory.getDAOUnderProject(User.class, project);
			Map<UUID, User> results = userDAO.get(new Condition("uuid", "=", replacement.getUser()));

			if (!results.isEmpty()) {
				User assignee = results.values().iterator().next();
				new Assignment(project, assignee, replacement).send();
			}
		}
	}
}
