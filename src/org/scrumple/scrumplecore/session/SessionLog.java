package org.scrumple.scrumplecore.session;

import java.util.*;

/**
 * A collection of events occurring during a {@code Session}.
 * @see Session
 */
public class SessionLog {
	private Session session;
	private final NavigableSet<SessionEvent> events = new TreeSet<>();
	
	/**
	 * Registers this log to a session.
	 * @param session session to log to
	 * @throws IllegalStateException if this log is already registered to a session
	 */
	void register(Session session) {
		if (this.session != null)
			throw new IllegalStateException("Already registered to session=" + session);
			
		this.session = session;
	}
	
	void logEvent(SessionEvent event) {
		events.add(event);
	}
	
	/**
	 * Returns all events occurring within a time range.
	 * @param start start of time range, inclusive
	 * @param end end of time range, inclusive
	 * @return all events occurring between {@code start} and {@code end}, inclusive
	 */
	public List<SessionEvent> getEvents(Date start, Date end) {
		SessionEvent 	from = new SessionEvent(start, null, null),
									to = new SessionEvent(end, null, null);
		
		return new ArrayList<>(events.subSet(from, true, to, true));
	}
}
