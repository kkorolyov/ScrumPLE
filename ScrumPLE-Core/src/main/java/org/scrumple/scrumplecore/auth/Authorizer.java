package org.scrumple.scrumplecore.auth;

import java.io.PrintWriter;

import javax.sql.DataSource;

import org.scrumple.scrumplecore.bean.User;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;

/**
 * Authorizes user actions.
 */
public class Authorizer {
	private static final Logger log = Logger.getLogger(Authorizer.class.getName(), Level.DEBUG, (PrintWriter[]) null);
	
	private final DataSource ds;
	
	/**
	 * Constructs a new authorizer.
	 * @param dataSource database providing authorization information
	 */
	public Authorizer(DataSource dataSource) {
		this.ds = dataSource;
	}
	
	boolean isAuthorized(User user) {
		// TODO
		return false;
	}
}
