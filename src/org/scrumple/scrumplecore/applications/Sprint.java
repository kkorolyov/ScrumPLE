package org.scrumple.scrumplecore.applications;

import java.util.*;



public class Sprint {
	private Set<Task> tasks;
	private Backlog backlog;
	
	public Sprint (Backlog sprintBacklog) {
		this.backlog = sprintBacklog;
		tasks = new HashSet<Task>();
	}
	
	public void setTasks() {
		tasks = backlog.getTasks();
	}

}
