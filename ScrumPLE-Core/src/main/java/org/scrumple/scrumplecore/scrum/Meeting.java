package org.scrumple.scrumplecore.scrum;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single meeting occurring between 2 points in time.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meeting implements Comparable<Meeting> {
	private String type;
	private Timestamp start, end;

	public Meeting(){};
	/**
	 * Constructs a new meeting.
	 * @param type description of meeting type
	 * @param start meeting start time in millis since epoch start
	 * @param end meeting end time in millis isnce epoch start
	 * @throws IllegalArgumentException if {@code start > end}
	 */
	@JsonCreator
	public Meeting(@JsonProperty("type") String type, @JsonProperty("start") long start, @JsonProperty("end") long end) {
		setType(type);
		setTime(start, end);
	}

	/**
	 * Clones a meeting.
	 * @param source meeting to clone
	 * @param start clone's start time in millis since epoch start
	 */
	public Meeting(Meeting source, long start) {
		this(source.type, start, start + source.getLength());
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

	/** @return meeting start time in millis since epoch start */
	public long getStart() {
		return start.getTime();
	}
	/** @return meeting end time in millis since epoch start */
	public long getEnd() {
		return end.getTime();
	}

	/** @return meeting length in millis */
	public long getLength() {
		return getEnd() - getStart();
	}

	/**
	 * Sets meeting start and end times.
	 * @param start start time in millis since epoch start
	 * @param end end time in millis since epoch start
	 * @throws IllegalArgumentException if {@code start > end}
	 */
	public void setTime(long start, long end) {
		validateRange(start, end);

		this.start = new Timestamp(start);
		this.end = new Timestamp(end);
	}
	private void validateRange(long start, long end) {
		if (start > end) throw new IllegalArgumentException("Start time is after end time: " + start + " > " + end);
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
