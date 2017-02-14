package org.scrumple.scrumplecore.resource;

import static org.scrumple.scrumplecore.assets.Assets.SYSTEM_DB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.scrumple.scrumplecore.auth.Credentials;
import org.scrumple.scrumplecore.database.DAO;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.User;
import org.scrumple.scrumplecore.assets.Assets;
import org.scrumple.scrumplecore.database.DataSourcePool;

import dev.kkorolyov.sqlob.persistence.Condition;
import dev.kkorolyov.sqlob.persistence.Session;

/**
 * Provides for retrieval of debug information.
 */
@Path("debug")
@Produces({MediaType.APPLICATION_XML})
public class DebugResource {
	private String systemDB = Assets.get(SYSTEM_DB);
	private DataSource systemDS = DataSourcePool.get(systemDB);
	
	/**
	 * Resets test database.
	 */
	@GET
	@Path("reset")
	@Produces(MediaType.TEXT_PLAIN)
	public String reset(@QueryParam("projects") @DefaultValue("10") String projects, @QueryParam("users") @DefaultValue("10") String users) {	// Test stub for populating projects
		long start = System.nanoTime();
		
		int numProjects = Integer.parseInt(projects),
				numUsers = Integer.parseInt(users);
		
		DAO<Project> projectDAO = SqlobDAOFactory.getProjectDAO();

		for (UUID id : projectDAO.getAll().keySet()) {
			projectDAO.remove(id);
		}
		for (int i = 0; i < numProjects; i++) {
			Project project = new Project("Project" + i, "description" + i, (i % 3 != 0), new User(new Credentials("owner " + i, "")));
			projectDAO.add(project);

			DAO<User> userDAO = SqlobDAOFactory.getDAOUnderProject(User.class, project);

			for (int j = 0; j < numUsers; j++) {
				userDAO.add(new User(new Credentials("user" + j, "password" + j), "display" + j, "role" + j));
			}
		}
		long 	end = System.nanoTime(),
					elapsedMS = (end - start) / 1000000;
		return "Database reset (" + systemDB + ") complete with " + numProjects + " projects, " + numUsers + " users per project in " + elapsedMS + "ms";
	}
}
