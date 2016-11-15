package org.scrumple.scrumplecore.resource;

import java.sql.SQLException;
import java.util.Set;

import javax.persistence.EntityNotFoundException;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.scrumple.scrumplecore.applications.Project;
import org.scrumple.scrumplecore.applications.User;
import org.scrumple.scrumplecore.assets.Assets.Config;
import org.scrumple.scrumplecore.auth.AuthenticationException;
import org.scrumple.scrumplecore.auth.Authenticator;
import org.scrumple.scrumplecore.database.DataSourcePool;

import dev.kkorolyov.sqlob.persistence.Condition;
import dev.kkorolyov.sqlob.persistence.Session;

/**
 * Provides user-facing authentication.
 */
@Path("auth")
public class AuthenticatorResource {
	private DataSource ds;
	
	/**
	 * Constructs a new authenticator resource.
	 */
	public AuthenticatorResource() {
		ds = DataSourcePool.get(Config.get(Config.SYSTEM_DB));
	}
	
	/**
	 * Authenticates a project-user pair.
	 * If the specified project is a valid project and the user formed from the specified handle and signature is a valid user, returns a key containing both project and user IDs.
	 * @param project project name
	 * @param handle user handle
	 * @param password user password encode in Base64
	 * @return key with which the specified project may be accessed under the specified user, or {@code null} if an authentication error occurs
	 * @throws SQLException if a database error occurs
	 * @throws AuthenticationException if an authentication error occurs
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String authenticate(@QueryParam("project") String project, @QueryParam("handle") String handle, @QueryParam("signature") String password) throws SQLException, AuthenticationException {	// TODO Form params
		String 	projectId,
						userId;
		String key = null;
		
		try (Session s = new Session(ds)) {	// TODO Move this to worker class?
			Set<Project> projects = s.get(Project.class, new Condition("projectName", "=", project));
			if (projects.isEmpty())
				throw new EntityNotFoundException("No such project: " + project);
			
			projectId = s.put(projects.iterator().next()).toString();	// TODO Change to getUUID()
		}
		DataSource projectDS = DataSourcePool.get(project);
		
		User user = new Authenticator(projectDS).get(handle, password);	// TODO Either both User and Project fetch in Authenticator or both here
		try (Session s = new Session(projectDS)) {
			userId = s.put(user).toString();
		}
		key = projectId + userId;
		
		return key;
	}
}
