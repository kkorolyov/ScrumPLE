package org.scrumple.scrumplecore.auth;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Set;

import javax.sql.DataSource;

import org.scrumple.scrumplecore.applications.User;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;
import dev.kkorolyov.sqlob.persistence.Condition;
import dev.kkorolyov.sqlob.persistence.Session;

/**
 * Authenticates session credentials.
 */
public class Authenticator {
	private static final Logger log = Logger.getLogger(Authenticator.class.getName(), Level.DEBUG, new PrintWriter(System.err));
	
	private final DataSource ds;
	
	/**
	 * Constructs a new authenticator.
	 * @param dataSource dataSource to database providing authentication information
	 */
	public Authenticator(DataSource dataSource) {
		this.ds = dataSource;
	}
	
	/**
	 * Returns the user backed by specified credentials.
	 * @param handle handle to use
	 * @param signature Base64-encoded password to use
	 * @return appropriate user
	 * @throws AuthenticationException if an authentication error occurs
	 * @throws SQLException if a database error occurs
	 */
	public User get(String handle, String signature) throws AuthenticationException, SQLException {
		try (Session s = new Session(ds)) {
			Set<User> users = s.get(User.class, new Condition("handle", "=", handle).and("password", "=", signature));
			if (users.isEmpty()) {
				String message = "Failed authentication for handle: " + handle;
				log.warning(message);
				throw new AuthenticationException(message);
			}
			User user = users.iterator().next();
			
			log.info("Authenticated user: " + user);
			return user;
		}
	}
}
