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
	
	public void setStateDate(Date d) {
		start = d.toLocalDate();
		convertedStartDate = d;
	}
	
	public void setEndDate(Date d) {
		end = d.toLocalDate();
		convertedEndDate = d;
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
