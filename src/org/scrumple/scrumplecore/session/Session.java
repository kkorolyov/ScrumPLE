package org.scrumple.scrumplecore.session;

import org.scrumple.scrumplecore.auth.AuthorizationException;
import org.scrumple.scrumplecore.session.action.Action;
import org.scrumple.scrumplecore.session.action.Reply;
import org.scrumple.scrumplecore.session.action.Request;

/**
 * Represents an interactive session with main application logic.
 */
public class Session {	// TODO Expand into self-contained object not reliant on DB info
	private final long id;
	private final SessionLog log;
	private final SessionManager manager;

	/**
	 * Constructs a new session.
	 * @param id session ID
	 * @param log logged actions
	 * @param manager pool managing this session
	 */
	public Session(long id, SessionLog log, SessionManager manager) {
		this.id = id;
		
		this.log = log;
		this.log.register(this);
		
		this.manager = manager;
	}
	
	/** @return {@code true} if this session is valid */
	public boolean isValid() {
		return manager.isValid(id);
	}
	/** 
	 * @param action action to execute
	 * @return {@code true} if this session can execute {@code action}
	 */
	public boolean isAuthorized(Action action) {
		// TODO
		return false;
	}
	
	/**
	 * Executes a request.
	 * @param request request to execute
	 * @return reply in response to request execution
	 * @throws AuthorizationException if this session is not authorized to execute {@code request}
	 */
	public Reply execute(Request request) throws AuthorizationException {
		// TODO
		return null;
	}
	
	/** @return log associated with this session */
	public SessionLog getLog() {
		return log;
	}
}
