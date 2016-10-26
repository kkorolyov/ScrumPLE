package org.scrumple.scrumplecore.session;

import java.util.Date;

import org.scrumple.scrumplecore.session.action.Action;

/**
 * A single event occurring during a {@code Session}.
 * @see Session
 */
public class SessionEvent implements Comparable<SessionEvent> {
	private final Date timestamp;
	private final Action action;
	private final String description;
	
	SessionEvent(Date timestamp, Action action, String description) {
		this.timestamp = timestamp;
		this.action = action;
		this.description = description;
	}

	/**
	 * @return {@code 0} if this event occurred at the same time as {@code o},
	 * a value less than {@code 0} if it occurred before,
	 * or a value greater than {@code 0} if it occurred after
	 */
	@Override
	public int compareTo(SessionEvent o) {
		return timestamp.compareTo(o.timestamp);
	}
}
