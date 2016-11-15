package org.scrumple.scrumplecore.resource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.scrumple.scrumplecore.applications.Project;
import org.scrumple.scrumplecore.applications.Role;
import org.scrumple.scrumplecore.applications.User;
import org.scrumple.scrumplecore.assets.Assets;
import org.scrumple.scrumplecore.database.DataSourcePool;

import dev.kkorolyov.sqlob.persistence.Condition;
import dev.kkorolyov.sqlob.persistence.Session;

/**
 * Handles project-specific requests.
 */
@Path("projects")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ProjectsResource {
	private DataSource ds;
	
	public ProjectsResource() {
		Assets.init();
		ds = DataSourcePool.get("ScrumPLE");
	}
	
	@GET
	public Set<Project> getProjects() throws SQLException {
		try (Session session = new Session(ds)) {
			return session.get(Project.class, (Condition) null);
		}
	}
	
	@GET
	@Path("{uuid}")
	public Project getProject(@PathParam("uuid") String uuid) throws SQLException {
		try (Session session = new Session(ds)) {
			return session.get(Project.class, UUID.fromString(uuid));
		}
	}
	
	@Path("{uuid}/users")
	public UsersResource getUsers(@PathParam("uuid") String uuid) throws SQLException {
		String projectSchema = null;
		
		try (Session s = new Session(ds)) {
			Project project = s.get(Project.class, UUID.fromString(uuid));
			if (project != null)
				projectSchema = project.getName();
		}
		return (projectSchema == null ? null : new UsersResource(DataSourcePool.get(projectSchema)));
	}
	
	public static void main(String[] args) throws SQLException {	// Test stub for populating projects
		Assets.init();
		
		try (Connection conn = DataSourcePool.get("").getConnection()) {
			conn.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS ScrumPLE");
			
			if (!conn.getAutoCommit())
				conn.commit();
		}
		int tests = 100;
		try (Session session = new Session(DataSourcePool.get("ScrumPLE"))) {
			for (int i = 0; i < tests; i++)
				session.put(new Project("Project" + i, "Description" + i));
			
			session.flush();
			
			for (Project project : session.get(Project.class, (Condition) null)) {
				try (Connection conn = DataSourcePool.get("").getConnection()) {
					conn.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS " + project.getName());
					
					if (!conn.getAutoCommit())
						conn.commit();
				}
				try (Session session2 = new Session(DataSourcePool.get(project.getName()))) {
					for (int i = 0; i < tests; i++)
						session2.put(new User("User" + i, "PASSWORD" + i, new Role("Role" + i)));
				}
			}
		}
	}
}
