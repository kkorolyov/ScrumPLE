package org.scrumple.scrumplecore.resource;

import java.util.Map.Entry;
import java.util.UUID;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MultivaluedMap;

import org.scrumple.scrumplecore.auth.AuthenticationException;
import org.scrumple.scrumplecore.auth.Authenticator;
import org.scrumple.scrumplecore.auth.Credentials;
import org.scrumple.scrumplecore.database.DAO;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.User;
import org.scrumple.scrumplecore.auth.UserSession;

import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * Provides endpoints for accessing projects.
 */
@Path("projects")
public class ProjectsResource extends CRUDResource<Project> {
	/**
	 * Constructs a new projects resource.
	 */
	public ProjectsResource() {
		super(SqlobDAOFactory.getProjectDAO());
	}

	/**
	 * Attempts to authenticate with a project.
	 * @param id project ID
	 * @param credentials credentials to authenticate
	 * @return session if authentication successful
	 * @throws AuthenticationException if {@code credentials} failed to authenticate
	 */
	@POST
	@Path("{uuid}/auth")
	public UserSession authenticate(@PathParam("uuid") UUID id, Credentials credentials) throws AuthenticationException {
		Project project = retrieve(id, null);
		DAO<User> userDAO = SqlobDAOFactory.getDAOUnderProject(User.class, project);
		DAO<UserSession> sessionDAO = SqlobDAOFactory.getDAOUnderProject(UserSession.class, project);

		return new Authenticator(userDAO, sessionDAO).authenticate(credentials);
	}

	@Override
	protected Condition parseQuery(MultivaluedMap<String, String> queryParams) {	// TODO Betterify
		String name = queryParams.getFirst("name");
		String handle = queryParams.getFirst("handle");

		if (name != null) {
			return new Condition("name", "LIKE", "%" + name + "%");
		} else if (handle != null) {
			Condition hasHandle = new Condition();

			for (Entry<UUID, Project> entry : dao.get((Condition) null).entrySet()) {
				DAO<Credentials> credDao = SqlobDAOFactory.getDAOUnderProject(Credentials.class, entry.getValue());

				if (!credDao.get(new Condition("handle", "=", handle)).isEmpty()) hasHandle.or("uuid", "=", entry.getKey().toString());
			}
			return hasHandle;
		} else {
			return null;
		}
	}

	/**
	 * @param id project id
	 * @return users resource under project {@code id}
	 */
	@Path("{uuid}/users")
	public UsersResource getUsers(@PathParam("uuid") UUID id) {
		return new UsersResource(retrieve(id, null));
	}

	@Path("{uuid}/stories")
	public UserStoryResource getStories(@PathParam("uuid") UUID id) {
		return new UserStoryResource(retrieve(id, null));
	}

	@Path("{uuid}/sprints")
	public SprintsResource getSprints(@PathParam("uuid") UUID id) {
		return new SprintsResource(retrieve(id, null));
	}

	@Path("{uuid}/stories/{uuid}/tasks")
	public TaskResource getTasks(@PathParam("uuid") UUID id) {
		return new TaskResource(retrieve(id, null));
	}

	/**
	 * @param id project id
	 * @return meetings resource under project {@code id}
	 */
	@Path("{uuid}/meetings")
	public MeetingsResource getMeetings(@PathParam("uuid") UUID id) {
		return new MeetingsResource(retrieve(id, null));
	}
}
