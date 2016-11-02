package org.scrumple.scrumplecore.applications;

import java.util.*;

import org.scrumple.scrumplecore.database.Saveable;

import java.time.*;
import java.sql.*
;
import java.sql.Date;

public class Sprint implements Saveable {
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

	@Override
	public List<Object> toData() {
		// TODO Auto-generated method stub
		return Arrays.asList(new Object[]{description, Date.valueOf(start), Date.valueOf(end)});

	}

	@Override
	public void fromData(List<Object> data) {
		Iterator <Object> it = data.iterator();
		
		this.description = (String) it.next();
		start = ((Date) it.next()).toLocalDate();
		end = ((Date) it.next()).toLocalDate();
		
	}

}
