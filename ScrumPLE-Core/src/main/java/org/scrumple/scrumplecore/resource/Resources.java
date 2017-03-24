package org.scrumple.scrumplecore.resource;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map.Entry;
import java.util.UUID;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.scrumple.scrumplecore.auth.Authorizer;
import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.auth.Credentials;
import org.scrumple.scrumplecore.database.DAO;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Meeting;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.User;
import org.scrumple.scrumplecore.scrum.Sprint;
import org.scrumple.scrumplecore.scrum.Task;
import org.scrumple.scrumplecore.scrum.UserStory;

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
		public SprintResource getSprints(@PathParam("uuid") UUID id) {
			return new SprintResource(retrieve(id, null));
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
	
	public static class TaskResource extends CRUDResource<Task> {
		/**
		 * Constructs a new task resource.
		 * @param project project that task belongs to
		 */
		public TaskResource(Project project) {
			super(SqlobDAOFactory.getDAOUnderProject(Task.class, project));
		}
		
		@Override
		protected Condition parseQuery(MultivaluedMap<String, String> queryParams) {
			String storyId = queryParams.getFirst("storyId");
			if(storyId != null) {
				return new Condition("storyId", "=", + Integer.valueOf(storyId));
			}	
		}
	}
	public static class SprintResource extends CRUDResource<Sprint> {
		/**
		 * Constructs a new sprint resource.
		 * @param project project of the sprint
		 */
		public SprintResource(Project project) {
			super(SqlobDAOFactory.getDAOUnderProject(Sprint.class, project));
		}
		
		@Override
		protected Condition parseQuery(MultivaluedMap<String, String> params) {
			String sprintNumber = params.getFirst("sprintNumber");
			if(sprintNumber!=null){
				return new Condition("sprintNumber", "=", Integer.valueOf(sprintNumber));
			}
			
		}
	}
	public static class UserStoryResource extends CRUDResource<UserStory> {
		/**
		 * Constructs a new user story resource.
		 * @param project project that the user story belongs to
		 */
		public UserStoryResource(Project project) {
			super(SqlobDAOFactory.getDAOUnderProject(UserStory.class, project));
		}
		
		@Override
		protected Condition parseQuery(MultivaluedMap<String, String> queryParams) {
			String sprintNumber = queryParams.getFirst("sprintNumber");
			if(sprintNumber != null){
				return new Condition("sprintNumber", "=", Integer.valueOf(sprintNumber));
			}
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

		/**
		 * Clones a meeting.
		 * @param id ID of meeting to clone
		 * @param start clone's start time in millis since epoch start
		 * @param headers request's HTTP headers
		 * @return clone's ID
		 */
		@POST
		@Path("{uuid}")
		public UUID clone(@PathParam("uuid") UUID id, long start, @Context HttpHeaders headers) {
			Meeting source = retrieve(id, headers);
			Meeting clone = new Meeting(source, start);

			return create(clone, headers);
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
