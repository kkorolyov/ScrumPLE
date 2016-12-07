package org.scrumple.scrumplecore.resource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

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
public class ProjectResource implements CRUDResource<Project> {
	private final String systemDB = Config.get(Config.SYSTEM_DB);
	private final DataSource ds = DataSourcePool.get(systemDB);
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public UUID create(Project data) throws SQLException {
		data.setName(data.getName().replaceAll("\\s+", "_"));	// Replace whitespace with underscore
		
		try (Session s = new Session(ds)) {
			if (!s.get(Project.class, new Condition("name", "=", data.getName())).isEmpty())
				throw new EntityExistsException("A project of the same name already exists: " + data.getName());
			
			try (Connection conn = ds.getConnection()) {
				conn.createStatement().executeUpdate("CREATE DATABASE " + data.getName());
			}
			return s.put(data);
		}
	}
	
	@GET
	@Path("{uuid}")
	@Override
	public Project retrieve(@PathParam("uuid") UUID id) throws SQLException {
		return getByUUID(id);
	}
	@GET
	@Override
	public Set<Entity> retrieveAll(@Context UriInfo uriInfo) throws SQLException {
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		//Map<UUID, Project> results = new HashMap<>();
		Set<Entity> results = new HashSet<>();
		
		Condition cond = buildCondition(queryParams.get("name"));
		try (Session s = new Session(ds)) {
			for (Project project : s.get(Project.class, cond))
				//results.put(s.getId(project), project);
				results.add(new Entity(s.getId(project), project));
		}
		return results;
	}
	private static Condition buildCondition(List<String> names) {
		Condition cond = null;
		
		if (names == null || names.isEmpty())
			cond = new Condition("isPrivate", "=", false);
		else {
			for (String name : names) {
				Condition currentCondition = new Condition("name", "=", name);
				
				if (cond == null)
					cond = currentCondition;
				else
					cond.or(currentCondition);
			}
		}
		return cond;
	}
	
	@PUT
	@Path("{uuid}")
	@Override
	public boolean update(@PathParam("uuid") UUID toUpdate, Project replacement)	throws SQLException {
		return false;
	}
	
	@DELETE
	@Path("{uuid}")
	@Override
	public Project delete(@PathParam("uuid") UUID id) throws SQLException {
		try (Session s = new Session(ds)) {
			Project project = s.get(Project.class, id);
			if (project == null)
				throw new EntityNotFoundException("No such project: " + id);
			
			s.drop(Project.class, id);
			try (Connection conn = ds.getConnection()) {
				conn.createStatement().executeUpdate("DROP DATABASE " + project.getName());
			}
			return project;
		}
	}

	/**
	 * @param id project id
	 * @return users resource for a project
	 * @throws SQLException if a database error occurs
	 */
	@Path("{uuid}/users")
	public UsersResource getUsers(@PathParam("uuid") UUID id) throws SQLException {
		return new UsersResource(getProjectDataSource(id));
	}
	
	/**
	 * @param id project id
	 * @return user story resource for a project
	 * @throws SQLException if a database error occurs
	 */
	@Path("{uuid}/userstories")
	public UserStoryResource getStory(@PathParam("uuid") UUID id) throws SQLException {
		return new UserStoryResource(getProjectDataSource(id));
	}
	
	/**
	 * @param id project id
	 * @return tasks resource for a project
	 * @throws SQLException if a database error occurs
	 */
	@Path("{uuid}/userstories/tasks")
	public TaskResource getTasks(@PathParam("uuid") UUID id) throws SQLException {
		return new TaskResource(getProjectDataSource(id));
	}
	
	private Project getByUUID(UUID id) throws SQLException {
		try (Session s = new Session(ds)) {
			Project project = s.get(Project.class, id);
			if (project == null)
				throw new EntityNotFoundException("No such project: " + id);
			
			return project;
		}
	}
	private DataSource getProjectDataSource(UUID id) throws SQLException {
		return DataSourcePool.get(getByUUID(id).getName());
	}
}
