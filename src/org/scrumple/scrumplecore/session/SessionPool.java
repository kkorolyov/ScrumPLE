package org.scrumple.scrumplecore.session;

/**
 * Manages a collection of {@code Session} objects.
 */
public interface SessionPool {
	boolean createSession(long sessionId);
	
	boolean terminateSession(long sessionId);
	
	Session getSession(long sessionId);
	
	boolean isValid(long sessionId);
}
