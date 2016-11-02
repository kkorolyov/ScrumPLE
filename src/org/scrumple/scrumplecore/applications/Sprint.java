package org.scrumple.scrumplecore.applications;

import java.util.*;
import java.time.*;
import java.sql.*
;
import java.sql.Date;

public class Sprint {
	private Set<Task> tasks;
	private Backlog backlog;
	private Period timeLeft;
	private LocalDate start, end, current;
	private Date convertedStartDate, convertedEndDate;
	
	@SuppressWarnings("deprecation")
	public Sprint (Backlog sprintBacklog, int year, int month, int dayOfMonth) {
		this.backlog = sprintBacklog;
		tasks = new HashSet<Task>();
		start = LocalDate.now();
		end = LocalDate.of(year, month, dayOfMonth);
		convertedStartDate = Date.valueOf(start);
		convertedEndDate = Date.valueOf(end);
	}
	
	public void setTasks() {
		tasks = backlog.getTasks();
	}
	
	public void setStateDate(int year, int month, int date) {
		start = LocalDate.of(year, month, date);
		convertedStartDate = Date.valueOf(start);
	}
	
	public void setEndDate(int year, int month, int date) {
		end = LocalDate.of(year, month, date);
		convertedEndDate = Date.valueOf(end);
	}
	
	public Date getConvertedStartDate() {
		return this.convertedStartDate;
	}
	
	public Date getConvertedEndDate() {
		return this.convertedEndDate;
	}
	
	public void timeLeft() {
		current = LocalDate.now();
		timeLeft = Period.between(current, end);
	}

}
