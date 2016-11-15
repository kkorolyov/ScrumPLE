package org.scrumple.scrumplecore.resource;

import java.sql.SQLException;
import java.util.Set;

import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.scrumple.scrumplecore.applications.User;

import dev.kkorolyov.sqlob.persistence.Condition;
import dev.kkorolyov.sqlob.persistence.Session;

/**
 * Handles user-specific requests for a project.
 */
@Produces(MediaType.APPLICATION_XML)
public class UsersResource {
	private DataSource ds;	// DataSource to specific Project
	
	/**
	 * Constructs a new users resource for a specified project.
	 * @param dataSource datasource to project
	 */
	public UsersResource(DataSource dataSource) {
		this.ds = dataSource;
	}
	
	/** @return all users under this project */
	@GET
	public Set<User> getUsers() throws SQLException {
		try (Session s = new Session(ds)) {
			return s.get(User.class, (Condition) null);
		}
	}
}
