package org.scrumple.scrumplecore.applications;

import java.util.*;

//General Backlog that holds a set of tasks
public class Backlog {
	private Set<Task> tasks;
	
	public Backlog() {
		tasks = new HashSet<Task>();
	}
	public void addTask(Task aTask) {
		tasks.add(aTask);
	}
	
	public Set<Task> getTasks() {
		return this.tasks;
	}

}
