package org.scrumple.scrumplecore.resource;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.scrumple.scrumplecore.applications.Task;
import org.scrumple.scrumplecore.applications.User;
import org.scrumple.scrumplecore.auth.AuthenticationException;
import org.scrumple.scrumplecore.service.ServiceLoader;

@Path("task")
public class TaskResource {
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String authenticate(@QueryParam("task") String name, @QueryParam("type") int type, @QueryParam("des") String des) {
		
		Task task;
		task = new Task(name, type, des);
		return task.getTaskName();
	}
}
