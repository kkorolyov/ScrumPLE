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
		String a = "sfdsafa";
		convertedStartDate = new Date(start.getYear(), start.getDayOfMonth()-1, start.getDayOfMonth());
		convertedEndDate = new Date(end.getYear(), end.getDayOfMonth()-1, end.getDayOfMonth());
	}
	
	public void setTasks() {
		tasks = backlog.getTasks();
	}
	
	public void setStateDate(int year, int month, int date) {
		start = LocalDate.of(year, month, date);
	}
	
	public void setEndDate(int year, int month, int date) {
		end = LocalDate.of(year, month, date);
	}
	
	public void timeLeft() {
		current = LocalDate.now();
		timeLeft = Period.between(current, end);
	}

}
