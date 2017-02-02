package org.scrumple.scrumplecore.bean;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

public class Sprint {
	private String description;
	private Set<Task> tasks;
	private Backlog backlog;
	private Period timeLeft;
	private LocalDate start, end, current;
	//private Date convertedStartDate, convertedEndDate;
	
	public Sprint (String des, Backlog sprintBacklog, int year, int month, int dayOfMonth) {
		this.description = des;
		this.backlog = sprintBacklog;
		tasks = new HashSet<Task>();
		start = LocalDate.now();
		end = LocalDate.of(year, month, dayOfMonth);
		//Not sure if I need these anymore.
		//convertedStartDate = Date.valueOf(start);
		//convertedEndDate = Date.valueOf(end);
	}
	
	public String getDes() {
		return description;
	}
	
	public void setTasks() {
		tasks = backlog.getTasks();
	}
	
	public void setStartDate(Date d) {
		start = d.toLocalDate();
		//convertedStartDate = d;
	}
	
	public void setEndDate(Date d) {
		end = d.toLocalDate();
		//convertedEndDate = d;
	}
	
	/*public Date getConvertedStartDate() {
		return this.convertedStartDate;
	}
	
	public Date getConvertedEndDate() {
		return this.convertedEndDate;
	}*/
	
	public void timeLeft() {
		current = LocalDate.now();
		timeLeft = Period.between(current, end);
	}
}
