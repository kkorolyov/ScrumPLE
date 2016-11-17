package org.scrumple.scrumplecore.resource;

import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.scrumple.scrumplecore.applications.Project;
import org.scrumple.scrumplecore.applications.User;
import org.scrumple.scrumplecore.assets.Assets.Config;
import org.scrumple.scrumplecore.database.DataSourcePool;

import dev.kkorolyov.sqlob.persistence.Session;

/**
 * Handles project-specific requests.
 */
@Path("{authkey}")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class SessionResource {
	private final DataSource ds;
	private final Project project;
	private final User user;
	
	/**
	 * Constructs a new projects resource using an authentication key.
	 * @param authKey combination of project and user to authenticate as
	 * @throws SQLException if a database error occurs
	 */
	public SessionResource(@PathParam("authkey") String authKey) throws SQLException {
		int midIndex = authKey.length() / 2;	// AuthKey is concatenated Type 4 UUIDs of Project and User
		UUID 	projectId = UUID.fromString(authKey.substring(0, midIndex)),
					userId = UUID.fromString(authKey.substring(midIndex, authKey.length()));
		
		try (Session s = new Session(DataSourcePool.get(Config.get(Config.SYSTEM_DB)))) {
			project = s.get(Project.class, projectId);
		}
		ds = DataSourcePool.get(project.getName());
		
		try (Session s = new Session(ds)) {
			user = s.get(User.class, userId);
		}
	}
	
	/** @return current user in this project */
	@GET
	@Path("profile")
	public User getProfile() {
		return user;
	}

	/** @return users resource for this project */
	@Path("users")
	public UsersResource getUsers() {
		return new UsersResource(ds);
	}
	@Path("tasks")
	public TaskResource getTask() {
		return new TaskResource(ds);
	}
}
