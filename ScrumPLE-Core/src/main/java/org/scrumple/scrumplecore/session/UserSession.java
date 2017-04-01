package org.scrumple.scrumplecore.session;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import javax.ws.rs.core.HttpHeaders;

import org.scrumple.scrumplecore.database.DAO;
import org.scrumple.scrumplecore.scrum.User;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;
import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * An application session consisting of a single user, start time, lifetime, and access token.
 */
public class UserSession {
	private static final long DEFAULT_DURATION = 15 * 60 * 1000;
	private static final Logger log = Logger.getLogger(UserSession.class.getName(), Level.DEBUG);

	private User user;
	private String token;
	private Timestamp start, end;

	/**
	 * Parses a user sesssion from a token in HTTP headers.
	 * @param headers HTTP headers to parse
	 * @param dao object providing access to sessions to search
	 * @return appropriate session, or {@code null} if not found
	 */
	public static UserSession fromHeaders(HttpHeaders headers, DAO<UserSession> dao) {
		List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);

		if (authHeaders != null && authHeaders.size() > 0) {
			String token = authHeaders.iterator().next();
			Map<UUID, UserSession> matchingSessions = dao.get(new Condition("token", "=", token));

			if (!matchingSessions.isEmpty()) {
				if (matchingSessions.size() > 1) log.severe(() -> dao + " has multiple sessions for token=" + token);	// Should not happen

				Entry<UUID, UserSession> sessionEntry = matchingSessions.entrySet().iterator().next();
				UserSession session = sessionEntry.getValue();

				if (session.isExpired()) {
					log.warning(() -> session + " expired " + (System.currentTimeMillis() - session.getEnd()) + "ms ago, removing...");
					dao.remove(sessionEntry.getKey());
				} else {
					log.debug(() -> "Found a valid session for token=" + token + ": " + session);
					return session;
				}
			}
		}
		return null;
	}

	public UserSession(){}

	/**
	 * Constructs a new session starting at the current system time and for the default duration.
	 * @param user user initiating session
	 */
	public UserSession(User user) {
		this(user, DEFAULT_DURATION);
	}
	/**
	 * Constructs a new session starting at the current system time and with a random access token.
	 * @param user user initiating session
	 * @param duration time until session expires in milliseconds
	 */
	public UserSession(User user, long duration) {
		this.user = user;

		token = generateToken();
		start = new Timestamp(System.currentTimeMillis());
		end = new Timestamp(start.getTime() + duration);
	}

	private static String generateToken() {
		StringBuilder tokenBuilder = new StringBuilder();
		Random rand = new Random();

		for (int i = 0; i < 2; i++) {
			tokenBuilder.append(UUID.randomUUID().toString().replaceAll("-", String.valueOf(rand.nextInt(10))));
		}
		return tokenBuilder.toString();
	}

	/** @return {@code true} if the current system time is past this session's end time */
	public boolean isExpired() {
		return System.currentTimeMillis() > end.getTime();
	}

	/** @return user owning session */
	public User getUser() {
		return user;
	}

	/** @return session access token */
	public String getToken() {
		return token;
	}

	/** @return session start time in ms */
	public long getStart() {
		return start.getTime();
	}
	/** @return session end time in ms */
	public long getEnd() {
		return end.getTime();
	}

	@Override
	public String toString() {
		return "UserSession{" +
					 "user=" + user +
					 ", token='" + token + '\'' +
					 ", start=" + start +
					 ", end=" + end +
					 '}';
	}
}
