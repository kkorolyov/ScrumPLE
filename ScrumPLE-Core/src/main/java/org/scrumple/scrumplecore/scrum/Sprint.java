package org.scrumple.scrumplecore.scrum;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sprint {
	private int sprintNumber;
	//private Period timeLeft;
	private Timestamp start, end;
	//private Date convertedStartDate, convertedEndDate;
	
	/*public Sprint (int sprintNumber, int syear, int smonth, int sdayOfMonth, int year, int month, int dayOfMonth) {
		this.sprintNumber = sprintNumber;
		start = LocalDate.of(syear, smonth, sdayOfMonth);
		end = LocalDate.of(year, month, dayOfMonth);
	}*/
	
	@JsonCreator
	public Sprint(@JsonProperty("sprintNumber") int sprintNumber, @JsonProperty("start") long start, @JsonProperty("end") long end) {
		setNumber(sprintNumber);
		setTime(start, end);
	}
	
	public int getNumber() {
		return sprintNumber;
	}
	
	public void setNumber(int sprintNumber) {
		this.sprintNumber = sprintNumber;
	}
	
	public void setTime(long start, long end) {
		legalRange(start, end);
		
		this.start = new Timestamp(start);
		this.end = new Timestamp(end);
	}
	
	private void legalRange(long start, long end) {
		if(start > end) throw new IllegalArgumentException("Start time is after end time");
	}
	
	public long getStart() {
		return start.getTime();
	}
	
	public long getEnd() {
		return end.getTime();
	}
	
	/*public void setStartDate(Date d) {
		start = d.toLocalDate();
	}
	
	public LocalDate getStart() {
		return start;
	}
	
	public void setEndDate(Date d) {
		end = d.toLocalDate();
	}
	
	public LocalDate getEnd() {
		return end;
	}

	public Period timeLeft() {
		current = LocalDate.now();
		timeLeft = Period.between(current, end);
		return timeLeft;
	}*/
}
