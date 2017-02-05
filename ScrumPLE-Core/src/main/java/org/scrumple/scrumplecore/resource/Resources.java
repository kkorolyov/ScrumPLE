package org.scrumple.scrumplecore.resource;

import dev.kkorolyov.sqlob.persistence.Condition;
import org.scrumple.scrumplecore.auth.AuthorizationException;
import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.auth.Credentials;
import org.scrumple.scrumplecore.bean.Project;
import org.scrumple.scrumplecore.bean.User;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MultivaluedMap;
import java.util.UUID;

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
		protected Project parseForm(MultivaluedMap<String, String> params) {
			String name = params.getFirst("name"),
					description = params.getFirst("description"),
					isPrivate = params.getFirst("isPrivate");

			if (name == null || description == null)
				throw new IllegalArgumentException("Missing some form parameters: name = " + name + ", description = " + description);

			return new Project(name, description, isPrivate != null);
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
		public UsersResource getUsers(@PathParam("uuid") UUID id) throws AuthorizationException {
			Project project = retrieve(id, null);
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
			super(SqlobDAOFactory.getDAOUnderProject(User.class, project), Authorizers.onlyUsersInDAO(SqlobDAOFactory.getDAOUnderProject(User.class, project)));	// TODO Use same DAO
		}

		@Override
		protected User parseForm(MultivaluedMap<String, String> params) {
			String handle = params.getFirst("handle"),
					password = params.getFirst("password");

			return new User(new Credentials(handle, password), null);	// TODO Null role for now
		}

		@Override
		protected Condition buildRetrieveCondition(MultivaluedMap<String, String> queryParams) {
			return null;
		}
	}
}