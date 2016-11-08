package org.scrumple.scrumplecore.auth;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import org.scrumple.scrumplecore.applications.User;
import org.scrumple.scrumplecore.database.Database;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;

/**
 * Authenticates session credentials.
 */
public class Authenticator {
	private static final Logger log = Logger.getLogger(Authenticator.class.getName(), Level.DEBUG, new PrintWriter(System.err));
	
	private final Database db;
	private final String digestAlgorithm;
	
	/**
	 * Constructs a new authenticator with SHA-256 as the digest algorithm.
	 * @see #Authenticator(Database, String)
	 */
	public Authenticator(Database database) {
		this(database, "SHA-256");
	}
	/**
	 * Constructs a new authenticator backed by the specified database.
	 * @param database database providing authentication information
	 * @param digestAlgorithm digest algorithm to use for computing password hash
	 */
	public Authenticator(Database database, String digestAlgorithm) {
		this.db = database;
		this.digestAlgorithm = digestAlgorithm;
	}
	
	public String authorize(String handle, String password) {	// TODO Would return a Response
		try {
			User user = get(handle, password);
		} catch (AuthenticationException e) {
		}
		
		// TODO Return session key randomized on user or error code if auth error
		
		return null;
	}
	
	/**
	 * Returns the user backed by specified credentials.
	 * @param handle handle to use
	 * @param password password to use
	 * @return appropriate user
	 * @throws AuthenticationException if an authentication error occurs
	 */
	public User get(String handle, String password) throws AuthenticationException {
		User user = null;
		try {
			MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
			byte[] hashedPass = md.digest(password.getBytes("UTF-8"));
			
			user = db.getUser(handle, hashedPass);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException | SQLException e) {
			log.exception(e);
		}
		if (user == null)
			throw new AuthenticationException("No such user: (" + handle + ", " + password + ")");
			
		return user;
	}
}
