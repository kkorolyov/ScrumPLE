package org.scrumple.scrumplecore.resource;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.scrumple.scrumplecore.applications.Task;
import org.scrumple.scrumplecore.applications.User;
import org.scrumple.scrumplecore.auth.AuthenticationException;
import org.scrumple.scrumplecore.database.Database;

import dev.kkorolyov.sqlob.persistence.Condition;
import dev.kkorolyov.sqlob.persistence.Session;

//@Path("tasks")
@Produces(MediaType.APPLICATION_XML)
public class TaskResource {
	
	private DataSource ds;
	
	public TaskResource(DataSource ds) {
		this.ds = ds;
	}
	@GET
	//@Produces(MediaType.APPLICATION_XML)
	public Set<Task> fetchTask(@QueryParam("type") int id) throws SQLException {
		Set <Task> tasks = new HashSet<Task>();
			
			try (Session s = new Session(ds)) {

				return s.get(Task.class, new Condition("taskType","=",id));
			}

	}
	
	@XmlRootElement
	public static class TaskList {
		@XmlElement
		private Collection<Task> tasks;
		
		public TaskList(){}
		TaskList(Collection<Task> tasks) {
			this.tasks = tasks;
		}
	}
}
