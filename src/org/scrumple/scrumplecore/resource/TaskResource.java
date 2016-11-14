package org.scrumple.scrumplecore.resource;

import java.io.File;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.scrumple.scrumplecore.applications.Task;
import org.scrumple.scrumplecore.applications.User;
import org.scrumple.scrumplecore.auth.AuthenticationException;
import org.scrumple.scrumplecore.database.Database;
import org.scrumple.scrumplecore.service.ServiceLoader;

@Path("tasks")
public class TaskResource {
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String fetchTask(@QueryParam("id") int id) {
		Task task = new Task(1, null);
		try {
			Database db = new Database("Project", null);
			task = db.load(id);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return task.getTaskDescription();
	}
}
