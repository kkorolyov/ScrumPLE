package org.scrumple.scrumplecore.scrum;

import java.sql.Timestamp;

/**
 * Represents a single meeting occurring between 2 points in time.
 */
public class Meeting {
	private String type;
	private Timestamp start, end;

	public Meeting(){};
	/**
	 * Constructs a new meeting.
	 * @param type description of meeting type
	 * @param start meeting start time
	 * @param end meeting end time, must be {@code >= start}
	 */
	public Meeting(String type, Timestamp start, Timestamp end) {
		setType(type);
		setTime(start, end);
	}

	/** @return meeting type */
	public String getType() {
		return type;
	}
	/**
	 * Sets meeting type.
	 * @param type new meeting type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/** @return meeting start time */
	public Timestamp getStart() {
		return start;
	}
	/** @return meeting end time */
	public Timestamp getEnd() {
		return end;
	}

	/**
	 * Sets meeting start and end times.
	 * @param start meeting start time
	 * @param end meeting end time, must be {@code >= start}
	 * @throws IllegalArgumentException if {@code start > end}
	 */
	public void setTime(Timestamp start, Timestamp end) {
		if (start.compareTo(end) > 0) throw new IllegalArgumentException("Start time may not be after end time: " + start + " > " + end);

		this.start = new Timestamp(start.getTime());
		this.end = new Timestamp(end.getTime());
	}
}
