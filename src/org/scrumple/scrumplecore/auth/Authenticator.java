package org.scrumple.scrumplecore.auth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Arrays;

import org.scrumple.scrumplecore.database.Database;
import org.scrumple.scrumplecore.database.SqlReader;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;

/**
 * Authenticates session credentials.
 */
public class Authenticator {
	private static final Logger log = Logger.getLogger(Authenticator.class.getName(), Level.DEBUG, new PrintWriter(System.err));
	private static final File authScript = new File("sql/authenticate.sql");
	
	private final Database db;
	
	/**
	 * Constructs a new authenticator backed by the specified database.
	 * @param database database providing authentication information
	 */
	public Authenticator(Database database) {
		this.db = database;
	}
	
	/**
	 * Authenticates credentials and creates a new session if authentication successful.
	 * @param credentials credentials to authenticate
	 * @return created session, or {@code null} if an authentication error occurred
	 */
	public long authenticate(String credentials, InetAddress source) {
		long id = -1;
		try {
			long userId = db.get(SqlReader.read(authScript, Arrays.asList(credentials)).iterator().next());
			if (userId >= 0)
				createSession(userId);
		} catch (FileNotFoundException e) {
			log.severe("Unable to find authentication script");
			log.exception(e);
		} catch (SQLException e) {
			log.severe("Database error occurred");
			log.exception(e);
		}
		return id;
	}
	private static Session createSession(long id) {
		Session session = new Session
	}
}
