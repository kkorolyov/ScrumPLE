package org.scrumple.scrumplecore.resource;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.scrumple.scrumplecore.bean.Task;
import org.scrumple.scrumplecore.database.Database;

@Path("tasklist")
public class TasksListResource {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String fetchSimilarTasks(@QueryParam("type") int type) {
		Set <Task> tasks= new HashSet<Task>();
		String list = "";
		try {
			Database db = new Database("Project", null);
			tasks = db.loadSimilarTasks(type);
		} catch (NamingException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Task t : tasks) {
			list = list.concat(t.getTaskDescription()+ t.getTaskType()+ "<br />");
		}
		return list;
		
	}
}
