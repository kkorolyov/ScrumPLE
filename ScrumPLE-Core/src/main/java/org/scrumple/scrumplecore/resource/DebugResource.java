package org.scrumple.scrumplecore.resource;

import static org.scrumple.scrumplecore.assets.Assets.SYSTEM_DB;

import java.time.Instant;
import java.util.UUID;

import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.scrumple.scrumplecore.assets.Assets;
import org.scrumple.scrumplecore.auth.AuthorizationException;
import org.scrumple.scrumplecore.auth.Authorizer;
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
	private static final Credentials debugger = new Credentials("d@bugg3r", "d3bug1t!");
	private static final Authorizer debuggerOnly = credentials -> {if (!credentials.equals(debugger)) throw new AuthorizationException(credentials);};

	private String systemDB = Assets.get(SYSTEM_DB);
	private DataSource systemDS = DataSourcePool.get(systemDB);
	
	/**
	 * Resets test database.
	 */
	@GET
	@Path("reset")
	@Produces(MediaType.TEXT_PLAIN)
	public String reset(@QueryParam("projects") @DefaultValue("10") String projects, @QueryParam("users") @DefaultValue("10") String users, @QueryParam("meetings") @DefaultValue("10") String meetings, @Context HttpHeaders headers) {	// Test stub for populating projects
		debuggerOnly.process(Credentials.fromHeaders(headers));

		long start = System.nanoTime();
		
		int numProjects = Integer.parseInt(projects);
		int numUsers = Integer.parseInt(users);
		int numMeetings = Integer.parseInt(meetings);

		DAO<Project> projectDAO = SqlobDAOFactory.getProjectDAO();

		for (UUID id : projectDAO.getAll().keySet()) {
			projectDAO.remove(id);
		}
		for (int i = 0; i < numProjects; i++) {
			Project project = new Project("Project" + i, "description" + i, (i % 3 != 0), new User(new Credentials("owner", "owner")));
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
		Instant now = Instant.now();
		for (int i = 0; i < num; i++) {
			long startSecondsOffset = i * 600, endSecondsOffset = startSecondsOffset + 300;
			meetingDAO.add(new Meeting("Debuggering", now.plusSeconds(startSecondsOffset), now.plusSeconds(endSecondsOffset)));
		}
	}
}
