package org.scrumple.scrumplecore.resource;

import java.sql.SQLException;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.scrumple.scrumplecore.applications.Project;
import org.scrumple.scrumplecore.assets.Assets.Config;
import org.scrumple.scrumplecore.database.DataSourcePool;

import dev.kkorolyov.sqlob.persistence.Session;

/**
 * Handles public project requests.
 */
@Path("projects")
@Produces(MediaType.APPLICATION_XML)
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ProjectsResource {
	private String systemDB = Config.get(Config.SYSTEM_DB);
	private DataSource ds = DataSourcePool.get(systemDB);
	
	/**
	 * Creates a new project and returns its UUID.
	 * @param data new project data
	 * @return UUID of new project
	 * @throws SQLException if a database error occurs
	 */
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String addProject(Project data) throws SQLException {	// TODO Exception if project exists
		try (Session s = new Session(ds)) {
			return s.put(data).toString();
		}
	}
}
