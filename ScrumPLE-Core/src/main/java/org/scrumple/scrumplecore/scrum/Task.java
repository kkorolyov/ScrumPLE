package org.scrumple.scrumplecore.scrum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Task {
	//private int taskType;  //Is it a feature, bug, etc.  Do we need a Label class, or can we just include it as a String field in Task? Current save method will require that we have a Label class.
	private String taskDescription;
	private int storyId;
	private boolean done;
	
	public Task() {
		this.taskDescription = null;
		//this.taskType = 0;
		this.done = false;
	}
	@JsonCreator
	public Task(@JsonProperty("storyId") int storyId, @JsonProperty("taskDescription") String taskDescription) {
		setId(storyId);
		setDescription(taskDescription);
		this.done = false;
	}
	
	public void setId(int storyId) {
		this.storyId = storyId;
	}
	public void setDescription(String taskDescription){
		this.taskDescription = taskDescription;
	}
	public int getStoryId() {
		return this.storyId;
	}
	
	public void setStoryId(int id) {
		this.storyId = id;
	}
	
	/*public void setTaskType(int newType) {
		this.taskType = newType;
	}*/
	
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
