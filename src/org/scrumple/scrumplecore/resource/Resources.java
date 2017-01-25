package org.scrumple.scrumplecore.resource;

import java.util.UUID;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MultivaluedMap;

import org.scrumple.scrumplecore.applications.Project;
import org.scrumple.scrumplecore.applications.User;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;

import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * Contains all main resource handlers.
 */
public class Resources {
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
		protected Condition buildRetrieveCondition(MultivaluedMap<String, String> queryParams) {
			Iterable<String> names = queryParams.get("name");
			Condition cond = null;
			
			if (names == null || !names.iterator().hasNext())
				cond = new Condition("isPrivate", "=", false);
			else {
				for (String name : names) {
					Condition currentCond = new Condition("name", "=", name);
					
					if (cond == null)
						cond = currentCond;
					else
						cond.or(currentCond);
				}
			}
			return cond;
		}
		
		/**
		 * @param id project id
		 * @return users resource under project matching {@code id}
		 */
		@Path("{uuid}/users")
		public UsersResource getUsers(@PathParam("uuid") UUID id) {
			Project project = retrieve(id);
			return project == null ? null : new UsersResource(project);
		}
	}
	
	/**
	 * Resource handler for users.
	 */
	public static class UsersResource extends CRUDResource<User> {
		/**
		 * Constructs a new users resource.
		 * @param project scope of users to handle
		 */
		public UsersResource(Project project) {
			super(SqlobDAOFactory.getDAOUnderProject(User.class, project));
		}

		@Override
		protected Condition buildRetrieveCondition(MultivaluedMap<String, String> queryParams) {
			return null;
		}
	}
}
