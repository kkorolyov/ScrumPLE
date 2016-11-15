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
	private final String digestAlgorithm;
	
	/**
	 * Constructs a new authenticator with SHA-256 as the digest algorithm.
	 * @see #Authenticator(DataSource, String)
	 */
	public Authenticator(DataSource dataSource) {
		this(dataSource, "SHA-256");
	}
	/**
	 * Constructs a new authenticator backed by the specified database.
	 * @param dataSource dataSource to database providing authentication information
	 * @param digestAlgorithm digest algorithm to use for computing password hash
	 */
	public Authenticator(DataSource dataSource, String digestAlgorithm) {
		this.ds = dataSource;
		this.digestAlgorithm = digestAlgorithm;
	}
	
	/**
	 * Returns the user backed by specified credentials.
	 * @param handle handle to use
	 * @param password password to use
	 * @return appropriate user
	 * @throws AuthenticationException if an authentication error occurs
	 * @throws SQLException if a database error occurs
	 */
	public User get(String handle, String password) throws AuthenticationException, SQLException {
		User user = null;
		
		try (Session s = new Session(ds)) {
			Set<User> users = s.get(User.class, new Condition("handle", "=", handle).and("password", "=", password));
			if (!users.isEmpty())
				user = users.iterator().next();
		}
		if (user == null)
			throw new AuthenticationException("No such user: (" + handle + ", " + password + ")");
			
		return user;
	}
}
