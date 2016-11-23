package org.scrumple.scrumplecore.resource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.scrumple.scrumplecore.applications.Task;
import org.scrumple.scrumplecore.applications.UserStory;

import dev.kkorolyov.sqlob.persistence.Condition;
import dev.kkorolyov.sqlob.persistence.Session;

@Produces(MediaType.APPLICATION_XML)
public class TaskResource {
	
	private DataSource ds;
	
	public TaskResource(DataSource ds) {
		this.ds = ds;
	}
	@GET
	public Set<Entity> fetchTask() throws SQLException {
			Set<Entity> resources = new HashSet<Entity>();
			try (Session s = new Session(ds)) {
				
				for(Task t : s.get(Task.class, (Condition) null))
				{
					resources.add(new Entity(s.put(t), t));
				}

				return resources;
			}

	}
	
	@Path("similar")
	@GET
	public Set<Task> fetchTask(@QueryParam("type") int id) throws SQLException {
		
		try (Session s = new Session(ds)) {

			return s.get(Task.class, new Condition("taskType","=",id));
		}
	}
	
	/**
	 * 
	 * @param story the user story
	 * @param type the task type
	 * @param des the task description
	 * @return String representation of the task uuid
	 * @throws SQLException
	 */
	@POST
	//@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	// Creates a task that is tied to a userstory. Entries in the database store the uuid of the userStory under the story column.
	public String createTask(@FormParam("userStory") String story, @FormParam("taskType") int type, @FormParam("taskDescription") String des) throws SQLException {
		try (Session s = new Session(ds)) {
			try (Connection conn = ds.getConnection()) {
				ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM userStory WHERE story =  " + "'"+story+"'");
				rs.next();
				UserStory u = new UserStory(rs.getString("story"));
				Task t = new Task(u, type, des);
				return s.put(t).toString();
			}
			
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

