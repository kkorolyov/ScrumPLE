package org.scrumple.scrumplecore.auth;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.scrumple.scrumplecore.database.DAO;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.User;
import org.scrumple.scrumplecore.session.UserSession;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;
import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * Authenticates users.
 */
public class Authenticator {
	private static final Logger log = Logger.getLogger(Authenticator.class.getName(), Level.DEBUG);
	
	private final Project project;
	
	/**
	 * Constructs a new authenticator.
	 * @param project project for which authentication is handled
	 */
	public Authenticator(Project project) {
		this.project = project;
	}

	public String authenticate(Credentials credentials) throws AuthenticationException {
		Map<UUID, User> users = SqlobDAOFactory.getDAOUnderProject(User.class, project)
																					 .get(new Condition("credentials", "=", credentials));

		if (users.isEmpty()) throw new AuthenticationException(project, credentials);
		else if (users.size() > 1) log.severe(() -> project + " has multiple users identified by " + credentials);	// Should not happen

		User user = users.values().iterator().next();
		DAO<UserSession> sessionDAO = SqlobDAOFactory.getDAOUnderProject(UserSession.class, project);
		Map<UUID, UserSession> existingSessions = sessionDAO.get(new Condition("user", "=", user));

		for (Entry<UUID, UserSession> entry : existingSessions.entrySet()) {
			sessionDAO.remove(entry.getKey());
			log.info(() -> "Removed existing session for " + user + ": " + entry.getValue());
		}
		UserSession session = new UserSession(user, 10 * 1000);

		log.info(() -> "Generated new session for " + user + ": " + session);
		return session.getToken();
	}
}
