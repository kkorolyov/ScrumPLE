package org.scrumple.scrumplecore.database;

import java.sql.SQLException;

/**
 * Thrown when an issue occurs during data access.
 */
public class DataAccessException extends RuntimeException {
	private static final long serialVersionUID = -6199759287810977609L;
	
	/**
	 * Constructs a new instance of this exception as a wrapper around a {@code SQLException}.
	 * @param cause {@code SQLException} wrapped by this exception
	 */
	public DataAccessException(SQLException cause) {
		super(cause);
	}
}
