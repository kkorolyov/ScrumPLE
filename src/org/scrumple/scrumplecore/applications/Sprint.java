package org.scrumple.scrumplecore.applications;

import java.util.*;
import java.time.*;



public class Sprint {
	private Set<Task> tasks;
	private Backlog backlog;
	private Period timeLeft;
	private LocalDate start;
	private LocalDate end;
	
	public Sprint (Backlog sprintBacklog, int year, int month, int dayOfMonth) {
		this.backlog = sprintBacklog;
		tasks = new HashSet<Task>();
		start = LocalDate.now();
		end = LocalDate.of(year, month, dayOfMonth);
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
		timeLeft = Period.between(start, end);
	}

}
