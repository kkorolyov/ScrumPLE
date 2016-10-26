package org.scrumple.scrumplecore.applications;

public class Task {
	private String taskName;
	private String taskDescription;
	private boolean done;
	
	public Task(String name, String taskDescription) {
		this.taskName = name;
		this.taskDescription = taskDescription;
		this.done = false;
	}
	
	public String getTaskName() {
		return this.taskName;
	}
	
	public String getTaskDescription() {
		return this.taskDescription;
	}
	
	public void updateDescription(String newDes) {
		taskDescription = newDes;
	}
	public boolean isFinished() {
		return done;
	}
}
