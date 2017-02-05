package org.scrumple.scrumplecore.resource;

import static org.scrumple.scrumplecore.assets.Assets.SYSTEM_DB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.Role;
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
	
	/** @return	all projects */
	@GET
	@Path("projects")
	public Set<Project> getProjects() throws SQLException {
		try (Session s = new Session(systemDS)) {
			return s.get(Project.class, (Condition) null);
		}
	}
	/** @return project specified by {@code uuid} */
	@GET
	@Path("projects/{uuid}")
	public Project getProject(@PathParam("uuid") String uuid) throws SQLException {
		try (Session s = new Session(systemDS)) {
			return s.get(Project.class, UUID.fromString(uuid));
		}
	}
	
	@GET
	@Path("projects/byName")
	@Produces(MediaType.TEXT_PLAIN)
	public String getProjectUUID(@QueryParam("name") String name) throws SQLException {
		try (Session s = new Session(systemDS)) {
			Project project = s.get(Project.class, new Condition("projectName", "=", name)).iterator().next();
			return s.put(project).toString();
		}
	}
	
	/** @return users under project specified by {@code uuid} */
	@GET
	@Path("projects/{uuid}/users")
	public Set<User> getUsers(@PathParam("uuid") String uuid) throws SQLException {
		try (Session s = new Session(systemDS)) {
			String projectDB = s.get(Project.class, UUID.fromString(uuid)).getName();

			try (Session s2 = new Session(DataSourcePool.get(projectDB))) {
				return s2.get(User.class, (Condition) null);
			}
		}
	}
	
	/**
	 * Resets test database.
	 */
	@GET
	@Path("reset")
	@Produces(MediaType.TEXT_PLAIN)
	public String reset(@QueryParam("projects") @DefaultValue("10") String projects, @QueryParam("users") @DefaultValue("10") String users) throws SQLException {	// Test stub for populating projects
		long start = System.nanoTime();
		
		int numProjects = Integer.parseInt(projects),
				numUsers = Integer.parseInt(users);
		
		try (Connection conn = DataSourcePool.get("").getConnection()) {
			conn.createStatement().executeUpdate("DROP DATABASE IF EXISTS " + systemDB);
			conn.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS " + systemDB);
			
			if (!conn.getAutoCommit())
				conn.commit();
		}
		try (Session session = new Session(systemDS)) {
			for (int i = 0; i < numProjects; i++)
				session.put(new Project("Project" + i, "Description" + i, (i % 3 == 0)));
			
			session.flush();
			
			for (Project project : session.get(Project.class, (Condition) null)) {
				String projectDB = project.getName();

				try (Connection conn = DataSourcePool.get("").getConnection()) {
					conn.createStatement().executeUpdate("DROP DATABASE IF EXISTS " + projectDB);
					conn.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS " + projectDB);
					
					if (!conn.getAutoCommit())
						conn.commit();
				}
				try (Session session2 = new Session(DataSourcePool.get(projectDB))) {
					for (int i = 0; i < numUsers; i++)
						session2.put(new User("User" + i, "PASSWORD" + i, new Role("Role" + i)));
				}
			}
		}
		long 	end = System.nanoTime(),
					elapsedMS = (end - start) / 1000000;
		return "Database reset (" + systemDB + ") complete with " + numProjects + " projects, " + numUsers + " users per project in " + elapsedMS + "ms";
	}
}
