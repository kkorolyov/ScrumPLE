package org.scrumple.scrumplecore.scrum;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

public class Sprint {
	private int sprintNumber;
	private Period timeLeft;
	private LocalDate start, end, current;
	//private Date convertedStartDate, convertedEndDate;
	
	public Sprint (int sprintNumber, int syear, int smonth, int sdayOfMonth, int year, int month, int dayOfMonth) {
		this.sprintNumber = sprintNumber;
		start = LocalDate.of(syear, smonth, sdayOfMonth);
		end = LocalDate.of(year, month, dayOfMonth);
	}
	
	public int getNumber() {
		return sprintNumber;
	}
	
	public void setStartDate(Date d) {
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
	}
}
