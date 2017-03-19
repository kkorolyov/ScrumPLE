package org.scrumple.scrumplecore.session;

import java.sql.Timestamp;
import java.util.Random;
import java.util.UUID;

import org.scrumple.scrumplecore.scrum.User;

/**
 * An application session consisting of a single user, start time, lifetime, and access token.
 */
public class UserSession {
	private static final long DEFAULT_DURATION = 15 * 60 * 1000;

	private User user;
	private String token;
	private Timestamp start, end;

	public UserSession(){}
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

	/** @return user owning session */
	public User getUser() {
		return user;
	}

	/** @return session access token, or {@code null} if session is expired */
	public String getToken() {
		tick();

		return token;
	}
	private void tick() {	// Invalidates token if current time past end
		if (System.currentTimeMillis() > end.getTime()) token = null;
	}
}
