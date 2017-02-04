package org.scrumple.scrumplecore.bean;

import java.sql.Timestamp;
import java.util.Random;
import java.util.UUID;

/**
 * An application session consisting of a single user, start time, lifetime, and access token.
 */
public class UserSession {
	private User user;
	private Timestamp start;
	private long lifetime;
	private String token;

	public UserSession(){}
	/**
	 * Constructs a new session starting at the current system time and with a random access token.
	 * @param user user initiating session
	 * @param lifetime time until session expires in milliseconds
	 */
	public UserSession(User user, long lifetime) {
		this.user = user;
		this.lifetime = lifetime;

		start = new Timestamp(System.currentTimeMillis());
		token = generateToken();
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

	/** @return session start time */
	public Timestamp getStart() {
		return start;
	}

	/** @return time from session start until expiration in milliseconds */
	public long getLifetime() {
		return lifetime;
	}

	/** @return session access token */
	public String getToken() {
		return token;
	}
}
