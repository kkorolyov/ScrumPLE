package org.scrumple.scrumplecore.auth;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.scrumple.scrumplecore.database.DAO;
import org.scrumple.scrumplecore.scrum.User;

import dev.kkorolyov.simplelogs.Level;
import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.format.Formatters;
import dev.kkorolyov.sqlob.utility.Condition;

/**
 * Authenticates users.
 */
public class Authenticator {
	private static final Logger log = Logger.getLogger(Level.DEBUG, Formatters.simple());
	
	private final DAO<User> userDAO;
	private final DAO<UserSession> sessionDAO;
	
	/**
	 * Constructs a new authenticator.
	 * @param userDAO DAO providing user data access
	 * @param sessionDAO DAO providing session data access
	 */
	public Authenticator(DAO<User> userDAO, DAO<UserSession> sessionDAO) {
		this.userDAO = userDAO;
		this.sessionDAO = sessionDAO;
	}

	public UserSession authenticate(Credentials credentials) throws AuthenticationException {
		Map<UUID, User> users = userDAO.get(new Condition("credentials", "=", credentials));

		if (users.isEmpty()) throw new AuthenticationException(credentials);
		else if (users.size() > 1) log.severe("Multiple users identified by {}", credentials);	// Should not happen

		User user = users.values().iterator().next();
		Map<UUID, UserSession> existingSessions = sessionDAO.get(new Condition("user", "=", user));

		for (Entry<UUID, UserSession> entry : existingSessions.entrySet()) {
			sessionDAO.remove(entry.getKey());
			log.info("Removed existing session for {}: {}", user, entry.getValue());
		}
		UserSession session = new UserSession(user);
		sessionDAO.add(session);

		log.info("Generated new session for {}: {}", user, session);
		return session;
	}
}
