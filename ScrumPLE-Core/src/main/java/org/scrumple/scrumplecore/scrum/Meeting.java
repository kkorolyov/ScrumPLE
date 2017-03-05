package org.scrumple.scrumplecore.scrum;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single meeting occurring between 2 points in time.
 */
public class Meeting implements Comparable<Meeting> {
	private String type;
	private Timestamp start, end;

	public Meeting(){};
	/**
	 * Constructs a new meeting.
	 * @param type description of meeting type
	 * @param start meeting start time
	 * @param end meeting end time, must be {@code >= start}
	 */
	@JsonCreator
	public Meeting(@JsonProperty("type") String type, @JsonProperty("start") Instant start, @JsonProperty("end") Instant end) {
		setType(type);
		setTime(start, end);
	}

	/**
	 * Clones a meeting.
	 * @param source meeting to clone
	 * @param start cloned meeting's start time
	 */
	@JsonCreator
	public Meeting(@JsonProperty("source") Meeting source, @JsonProperty("start") Instant start) {
		this(source.type, start, start.plusMillis(source.getLength()));
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
	public Instant getStart() {
		return start.toInstant();
	}
	/** @return meeting end time */
	public Instant getEnd() {
		return end.toInstant();
	}

	/** @return meeting length in millis */
	public long getLength() {
		return getStart().until(getEnd(), ChronoUnit.MILLIS);
	}

	/**
	 * Sets meeting start and end times.
	 * @param start meeting start time
	 * @param end meeting end time, must be {@code >= start}
	 * @throws IllegalArgumentException if {@code start > end}
	 */
	public void setTime(Instant start, Instant end) {
		validateRange(start, end);

		this.start = Timestamp.from(start);
		this.end = Timestamp.from(end);
	}
	private void validateRange(Instant start, Instant end) {
		if (start.isAfter(end)) throw new IllegalArgumentException("Start time is after end time: " + start + " > " + end);
	}

	/**
	 * Compares this meeting to another by start time
	 * @param o meeting to compare to
	 * @return negative if this meeting starts before {@code o}, positive if after, {@code 0} if at the same time
	 */
	@Override
	public int compareTo(Meeting o) {
		return start.compareTo(o.start);
	}
}
