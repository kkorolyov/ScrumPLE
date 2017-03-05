package org.scrumple.scrumplecore.resource;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map.Entry;
import java.util.UUID;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MultivaluedMap;

import org.scrumple.scrumplecore.auth.Authorizer;
import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.auth.Credentials;
import org.scrumple.scrumplecore.database.DAO;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Meeting;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.User;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * Contains all main resource handlers.
 */
public class Resources {
	private static final Logger log = Logger.getLogger(Resources.class.getName(), Logger.Level.DEBUG);

	/**
	 * Resource handler for projects.
	 */
	@Path("projects")
	public static class ProjectsResource extends CRUDResource<Project> {
		/**
		 * Constructs a new projects resource.
		 */
		public ProjectsResource() {
			super(SqlobDAOFactory.getProjectDAO());
		}

		@Override
		protected Project parseForm(MultivaluedMap<String, String> params) {
			String name = params.getFirst("name"),
					description = params.getFirst("description"),
					visible = params.getFirst("visible"),
					ownerHandle = params.getFirst("handle"),
					ownerPassword = params.getFirst("password"),
					ownerDisplayName = params.getFirst("displayName");

			if (name == null || description == null || ownerHandle == null || ownerPassword == null)
				throw new IllegalArgumentException("Missing some form parameters");

			User owner = new User(new Credentials(ownerHandle, ownerPassword), ownerDisplayName, null);
			return new Project(name, description, visible != null, owner);
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

		/**
		 * @param id project id
		 * @return meetings resource under project {@code id}
		 */
		@Path("{uuid}/meetings")
		public MeetingsResource getMeetings(@PathParam("uuid") UUID id) {
			return new MeetingsResource(retrieve(id, null));
		}
	}
	
	public static class UsersResource extends CRUDResource<User> {
		/**
		 * Constructs a new users resource.
		 * @param project users scope
		 */
		public UsersResource(Project project) {
			super(SqlobDAOFactory.getDAOUnderProject(User.class, project));

			Authorizer onlyOwner = Authorizers.onlyOwner(project);

			setAuthorizers(onlyOwner, Authorizers.ALL, onlyOwner, onlyOwner);
		}

		@Override
		protected User parseForm(MultivaluedMap<String, String> params) {
			String handle = params.getFirst("handle"),
					password = params.getFirst("password");

			return new User(new Credentials(handle, password));
		}

		@Override
		protected Condition parseQuery(MultivaluedMap<String, String> queryParams) {
			return null;
		}
	}

	public static class MeetingsResource extends CRUDResource<Meeting> {
		/**
		 * Constructs a new meetings resource.
		 * @param project meetings scope
		 */
		public MeetingsResource(Project project) {
			super(SqlobDAOFactory.getDAOUnderProject(Meeting.class, project));

			setAuthorizers(Authorizers.onlyUsers(project));
		}

		@Override
		protected Meeting parseForm(MultivaluedMap<String, String> params) {
			return null;
		}

		@Override
		protected Condition parseQuery(MultivaluedMap<String, String> queryParams) {
			String startString = queryParams.getFirst("start");
			String endString = queryParams.getFirst("end");

			Instant start = (startString == null) ? null : Instant.ofEpochMilli(Long.parseLong(startString));
			Instant end = (endString == null) ? null : Instant.ofEpochMilli(Long.parseLong(endString));

			Condition condition = (start == null) ? null : new Condition("start", ">=", Timestamp.from(start));
			if (end != null) {
				Condition endCondition = new Condition("start", "<=", Timestamp.from(end));

				if (condition == null) condition = endCondition;
				else condition.and(endCondition);
			}
			return condition;
		}
	}
}
