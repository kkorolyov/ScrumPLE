package org.scrumple.scrumplecore.resource;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
	
	/** @return names of all users under this project */
	@GET
	public NameList listNames() throws SQLException {
		Set<String> names = new HashSet<>();
		
		try (Session s = new Session(ds)) {
			for (User user : s.get(User.class, (Condition) null))
				names.add(user.getHandle());
		}
		return new NameList(names);
	}
	
	@XmlRootElement
	public static class NameList {
		@XmlElement
		private Collection<String> names;
		
		public NameList(){}
		NameList(Collection<String> names) {
			this.names = names;
		}
	}
}
