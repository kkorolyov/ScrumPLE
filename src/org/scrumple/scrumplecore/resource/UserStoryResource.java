package org.scrumple.scrumplecore.resource;

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
public class UserStoryResource {

	private DataSource ds;
	
	public UserStoryResource(DataSource ds) {
		this.ds = ds;
	}
	
	@GET 
	public Set<Entity> fetchStories() throws SQLException{
		Set<Entity> resources = new HashSet<Entity>();
		try (Session s = new Session(ds)) {
			
			for(UserStory userStory : s.get(UserStory.class, (Condition) null))
			{
				resources.add(new Entity(s.put(userStory), userStory));
			}

			return resources;
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	public String createUserStory(UserStory story) throws SQLException {
		
		try (Session s = new Session(ds)) {
			return s.put(story).toString();
		}
	}
	
}
