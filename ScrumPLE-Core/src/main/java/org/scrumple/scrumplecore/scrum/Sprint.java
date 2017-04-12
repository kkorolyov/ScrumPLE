package org.scrumple.scrumplecore.scrum;

import java.sql.Timestamp;

/**
 * Representation of a single sprint.
 */
public class Sprint {
	private int sprintNumber;
	private Timestamp start, end;

	public Sprint(){
		
	}
	
	/**
	 * Creates a new sprint
	 * @param sprintNumber the sprint iteration
	 * @param start the start time in millis since epoch
	 * @param end the end time in millis since epoch
	 * @throws IllegalArgumentException if {@code start > end}
	 */
	public Sprint(int sprintNumber, long start, long end) {
		setSprintNumber(sprintNumber);
		setTime(start, end);
	}
	
	/**
	 * @return sprintNumber
	 */
	public int getSprintNumber() {
		return sprintNumber;
	}
	
	/**
	 * sets sprintNumber
	 * @param sprintNumber sprint number of sprint
	 */
	public void setSprintNumber(int sprintNumber) {
		this.sprintNumber = sprintNumber;
	}
	
	/**
	 * Sets start and end time of sprint
	 * @param start start time of sprint in millis since epoch
	 * @param end end time of sprint in millis since epoch
	 */
	public void setTime(long start, long end) {
		legalRange(start, end);
		
		this.start = new Timestamp(start);
		this.end = new Timestamp(end);
	}
	
	/**
	 * Checks if the start time is before the end time
	 * @param start start time of sprint in millis since epoch
	 * @param end end time of sprint in millis since epoch
	 * @throws IllegalArgumentException if {@code start > end}
	 */
	private void legalRange(long start, long end) {
		if(start > end) throw new IllegalArgumentException("Start time is after end time");
	}
	
	/**
	 * @return start time
	 */
	public long getStart() {
		return start.getTime();
	}
	 /**
	  * @return end time
	  */
	public long getEnd() {
		return end.getTime();
	}
}
