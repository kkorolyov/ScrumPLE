package org.scrumple.scrumplecore.auth;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;
import dev.kkorolyov.sqlob.persistence.Condition;
import org.scrumple.scrumplecore.bean.Project;
import org.scrumple.scrumplecore.bean.User;
import org.scrumple.scrumplecore.bean.UserSession;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;

import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;

/**
 * Authenticates users.
 */
public class Authenticator {
	private static final Logger log = Logger.getLogger(Authenticator.class.getName(), Level.DEBUG, (PrintWriter[]) null);
	
	private final Project project;
	
	/**
	 * Constructs a new authenticator.
	 * @param project project for which authentication is handled
	 */
	public Authenticator(Project project) {
		this.project = project;
	}

	public String authenticate(String handle, String password) throws AuthenticationException {	// TODO Not for use yet
		Map<UUID, User> users = SqlobDAOFactory.getDAOUnderProject(User.class, project).get(
				new Condition("handle", "=", handle)
						.and("password", "=", password));

		if (users.isEmpty()) {
			throw new AuthenticationException(handle);
		}
		UserSession session = new UserSession(users.values().iterator().next(), 10000);

		return session.getToken();
	}
}
