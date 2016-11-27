package org.scrumple.scrumplecore.resource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.scrumple.scrumplecore.applications.Project;
import org.scrumple.scrumplecore.assets.Assets.Config;
import org.scrumple.scrumplecore.database.DataSourcePool;

import dev.kkorolyov.sqlob.persistence.Condition;
import dev.kkorolyov.sqlob.persistence.Session;

/**
 * Handles project requests.
 */
@Path("projects")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ProjectsResource {
	private final String systemDB = Config.get(Config.SYSTEM_DB);
	private final DataSource ds = DataSourcePool.get(systemDB);
	
	/**
	 * Constructs a new projects resource using an authentication key.
	 * @param authKey combination of project and user to authenticate as
	 * @throws SQLException if a database error occurs
	 */
	/*public ProjectResource(@PathParam("authkey") String authKey) throws SQLException {
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
	}*/
	
	@GET
	public Set<Entity> getProjects(@QueryParam("name") String name) throws SQLException {
		Set<Entity> resources = new HashSet<>();
		
		try (Session s = new Session(ds)) {
			for (Project project : s.get(Project.class, (name == null ? (Condition) null : new Condition("name", "=", name)))) {
				if ((name == null && !project.isPrivate()) || (name != null && name.equals(project.getName())))	// If no name, add all publics; else, add only name matches
					resources.add(new Entity(s.getId(project), project));
			}
		}
		return resources;
	}
	/**
	 * Retrieves a project for a specific UUID.
	 * @param uuid project uuid
	 * @return appropriate project
	 * @throws SQLException if a database error occurs
	 */
	@GET
	@Path("{uuid}")
	public Project getProject(@PathParam("uuid") String uuid) throws SQLException {
		return getByUUID(uuid);
	}
	
	/**
	 * Creates a new project and returns its UUID.
	 * @param data new project data
	 * @return UUID of new project
	 * @throws SQLException if a database error occurs
	 */
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String addProject(Project data) throws SQLException {
		data.setName(data.getName().replaceAll("\\s+", "_"));	// Replace whitespace with underscore
		
		try (Session s = new Session(ds)) {
			if (!s.get(Project.class, new Condition("name", "=", data.getName())).isEmpty())
				throw new EntityExistsException("A project of the same name already exists: " + data.getName());
			
			try (Connection conn = ds.getConnection()) {
				conn.createStatement().executeUpdate("CREATE DATABASE " + data.getName());
			}
			return s.put(data).toString();
		}
	}
	
	/**
	 * Removes a project.
	 * @param uuid project uuid
	 * @return removed project
	 * @throws SQLException if a database error occurs
	 */
	@DELETE
	@Path("{uuid}")
	public Project removeProject(@PathParam("uuid") String uuid) throws SQLException {
		try (Session s = new Session(ds)) {
			UUID id = UUID.fromString(uuid);
			
			Project project = s.get(Project.class, id);
			if (project == null)
				throw new EntityNotFoundException("No such project: " + uuid);
			
			s.drop(Project.class, id);
			try (Connection conn = ds.getConnection()) {
				conn.createStatement().executeUpdate("DROP DATABASE " + project.getName());
			}
			return project;
		}
	}

	/**
	 * @param uuid project uuid
	 * @return users resource for a project
	 * @throws SQLException if a database error occurs
	 */
	@Path("{uuid}/users")
	public UsersResource getUsers(@PathParam("uuid") String uuid) throws SQLException {
		return new UsersResource(getProjectDataSource(uuid));
	}
	
	/**
	 * @param uuid project uuid
	 * @return user story resource for a project
	 * @throws SQLException if a database error occurs
	 */
	@Path("{uuid}/userstories")
	public UserStoryResource getStory(@PathParam("uuid") String uuid) throws SQLException {
		return new UserStoryResource(getProjectDataSource(uuid));
	}
	/**
	 * @param uuid project uuid
	 * @return tasks resource for a project
	 * @throws SQLException if a database error occurs
	 */
	@Path("{uuid}/tasks")
	public TaskResource getTasks(@PathParam("uuid") String uuid) throws SQLException {
		return new TaskResource(getProjectDataSource(uuid));
	}
	
	private Project getByUUID(String uuid) throws SQLException {
		try (Session s = new Session(ds)) {
			Project project = s.get(Project.class, UUID.fromString(uuid));
			if (project == null)
				throw new EntityNotFoundException("No such project: " + uuid);
			
			return project;
		}
	}
	private DataSource getProjectDataSource(String uuid) throws SQLException {
		return DataSourcePool.get(getByUUID(uuid).getName());
	}
}
