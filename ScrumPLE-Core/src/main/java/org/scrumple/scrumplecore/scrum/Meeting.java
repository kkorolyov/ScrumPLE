package org.scrumple.scrumplecore.scrum;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Represents a single meeting occurring between 2 points in time.
 */
public class Meeting implements Comparable<Meeting> {
	/** Constant for daily repeat */
	public static final long DAILY = 24 * 60 * 60 * 1000;
	/** Constant for weekly repeat */
	public static final long WEEKLY = DAILY * 7;

	private String type;
	private Timestamp start, end;
	private long repeatInterval;

	public Meeting(){};
	/**
	 * Constructs a new meeting.
	 * @param type description of meeting type
	 * @param start meeting start time
	 * @param end meeting end time, must be {@code >= start}
	 */
	@JsonCreator
	public Meeting(String type, Instant start, Instant end) {
		setType(type);
		setTime(start, end);
	}

	/**
	 * Returns all occurrences of this meeting between a time range.
	 * @param start range start
	 * @param end range end
	 * @return all occurrences of this meeting between {@code start} and {@code end}, ordered by start time
	 * @throws IllegalArgumentException if {@code start > end}
	 */
	public Set<Meeting> getAllOccurrences(Instant start, Instant end) {
		validateRange(start, end);

		Set<Meeting> all = new TreeSet<>();
		Instant currentStart = getStart(), currentEnd = getEnd();

		while (between(currentStart, start, end)) {
			all.add(new Meeting(type, currentStart, currentEnd));

			if (repeatInterval <= 0) break;	// Maximum 1 meeting (original) to return

			currentStart = currentStart.plusMillis(repeatInterval);
			currentEnd = currentEnd.plusMillis(repeatInterval);
		}
		return all;
	}
	private static boolean between(Instant instant, Instant rangeStart, Instant rangeEnd) {
		return rangeStart.compareTo(instant) <= 0 && instant.compareTo(rangeEnd) <= 0;
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

	/** @return {@code true} if this meeting repeats */
	public boolean repeats() {
		return repeatInterval > 0;
	}

	/** @return meeting repeat interval in milliseconds */
	public long getRepeat() {
		return repeatInterval;
	}
	/**
	 * Sets the repeat interval of this meeting.
	 * @param interval milliseconds after which to repeat this meeting
	 */
	public void setRepeat(long interval) {
		repeatInterval = Math.max(0, interval);
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
