package org.scrumple.scrumplecore.scrum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Task {
	private String description;
	private UserStory story;
	private boolean done;
	
	public Task() {
		this.description = null;
		this.done = false;
	}
	public Task(UserStory story, String description) {
		setStory(story);
		setDescription(description);
		this.done = false;
	}
	
	public void setStory(UserStory story) {
		this.story = story;
	}
	public void setDescription(String taskDescription){
		this.description = taskDescription;
	}
	public UserStory getStory() {
		return this.story;
	}
	
	/*public void setTaskType(int newType) {
		this.taskType = newType;
	}*/
	
	public String getDescription() {
		return this.description;
	}
	
	public boolean isFinished() {
		return done;
	}
}
