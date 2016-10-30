package org.scrumple.scrumplecore.applications;

public class Task {
	private String taskName;
	private String taskType;  //Is it a feature, bug, etc.  Do we need a Label class, or can we just include it as a String field in Task? Current save method will require that we have a Label class.
	private String taskDescription;
	private boolean done;
	
	public Task(String name, String type, String taskDescription) {
		this.taskName = name;
		this.taskType = type;
		this.taskDescription = taskDescription;
		this.done = false;
	}
	
	public String getTaskName() {
		return this.taskName;
	}
	
	public String getTaskType() {
		return this.taskType;
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
