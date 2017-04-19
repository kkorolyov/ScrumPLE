package org.scrumple.scrumplecore.resource;

import static org.scrumple.scrumplecore.assets.Assets.SYSTEM_DB;

import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.scrumple.scrumplecore.assets.Assets;
import org.scrumple.scrumplecore.auth.AuthorizationException;
import org.scrumple.scrumplecore.auth.Credentials;
import org.scrumple.scrumplecore.database.DAO;
import org.scrumple.scrumplecore.database.DataSourcePool;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Meeting;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.User;

/**
 * Provides for retrieval of debug information.
 */
@Path("debug")
@Produces({MediaType.APPLICATION_XML})
public class DebugResource {
	private static final String DEBUG_TOKEN = "AFi09g34iyv930mvy09i3vyq309iybbMNW$ykehlmkrstohrt";
	private static final User debugger = new User(new Credentials("d@bugg3r", "d3bug1t!"), "debugger");

	private String systemDB = Assets.get(SYSTEM_DB);
	private DataSource systemDS = DataSourcePool.get(systemDB);
	
	/**
	 * Resets test database.
	 */
	@GET
	@Path("reset")
	@Produces(MediaType.TEXT_PLAIN)
	public String reset(@QueryParam("projects") @DefaultValue("10") String projects, @QueryParam("users") @DefaultValue("10") String users, @QueryParam("meetings") @DefaultValue("10") String meetings, @Context HttpHeaders headers) {	// Test stub for populating projects
		authorize(headers);

		long start = System.nanoTime();
		
		int numProjects = Integer.parseInt(projects);
		int numUsers = Integer.parseInt(users);
		int numMeetings = Integer.parseInt(meetings);

		DAO<Project> projectDAO = SqlobDAOFactory.getProjectDAO();

		for (UUID id : projectDAO.getAll().keySet()) {
			projectDAO.remove(id);
		}
		for (int i = 0; i < numProjects; i++) {
			Project project = new Project("Project" + i, "description" + i, new User(new Credentials("owner", "owner")));
			projectDAO.add(project);

			generateUsers(project, numUsers);
			generateMeetings(project, numMeetings);
		}
		long end = System.nanoTime(), elapsedMS = (end - start) / 1000000;
		return "Database reset (" + systemDB + ") complete with "
					 + numProjects + " projects, "
					 + numUsers + " users per project, "
					 + numMeetings + " meetings per project, "
					 + "in " + elapsedMS + "ms";
	}
	private static void generateUsers(Project project, int num) {
		DAO<User> userDAO = SqlobDAOFactory.getDAOUnderProject(User.class, project);
		for (int i = 0; i < num; i++) {
			userDAO.add(new User(new Credentials("user" + i, "password" + i), "display" + i, "role" + i));
		}
	}
	private static void generateMeetings(Project project, int num) {
		DAO<Meeting> meetingDAO = SqlobDAOFactory.getDAOUnderProject(Meeting.class, project);
		long now = System.currentTimeMillis();
		for (int i = 0; i < num; i++) {
			long startOffset = i * 600 * 1000, endOffset = startOffset + 300 * 1000;
			meetingDAO.add(new Meeting("Debuggering", now + startOffset, now + endOffset));
		}
	}

	private void authorize(HttpHeaders headers) {
		List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
		if (authHeaders.size() <= 0) throw new AuthorizationException(debugger, "Debugging");

		String token = authHeaders.iterator().next();
		if (!DEBUG_TOKEN.equals(token)) throw new AuthorizationException(debugger, "Debugging");
	}
}
