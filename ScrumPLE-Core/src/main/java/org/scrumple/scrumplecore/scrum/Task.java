package org.scrumple.scrumplecore.scrum;

public class Task {
	private int taskType;  //Is it a feature, bug, etc.  Do we need a Label class, or can we just include it as a String field in Task? Current save method will require that we have a Label class.
	private String taskDescription;
	private UserStory story;
	private boolean done;
	
	public Task() {
		this.taskDescription = null;
		this.taskType = 0;
		this.done = false;
	}
	public Task( UserStory story, int type, String taskDescription) {
		this.taskDescription = taskDescription;
		this.story = story;
		this.taskType = type;
		this.done = false;
	}
	
	public int getTaskType() {
		return this.taskType;
	}
	
	public UserStory getStory() {
		return this.story;
	}
	
	public void setStory(UserStory aStory) {
		this.story = aStory;
	}
	
	public void setTaskType(int newType) {
		this.taskType = newType;
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