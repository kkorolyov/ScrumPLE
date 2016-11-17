package org.scrumple.scrumplecore.resource;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.scrumple.scrumplecore.applications.Task;


import dev.kkorolyov.sqlob.persistence.Condition;
import dev.kkorolyov.sqlob.persistence.Session;

@Produces(MediaType.APPLICATION_XML)
public class TaskResource {
	
	private DataSource ds;
	
	public TaskResource(DataSource ds) {
		this.ds = ds;
	}
	@GET
	public Set<Task> fetchTask() throws SQLException {
			
			try (Session s = new Session(ds)) {

				return s.get(Task.class, (Condition) null);
			}

	}
	
	@Path("similar")
	@GET
	public Set<Task> fetchTask(@QueryParam("type") int id) throws SQLException {
		
		try (Session s = new Session(ds)) {

			return s.get(Task.class, new Condition("taskType","=",id));
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	public String createTask(Task t) throws SQLException {
		try (Session s = new Session(ds)) {
			return s.put(t).toString();
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

