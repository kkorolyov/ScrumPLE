package org.scrumple.scrumplecore.scrum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a task
 */
public class Task {
	private String description;
	private UserStory story;
	private boolean done;

	/**
	 * Creates a null task.
	 */
	public Task() {
		this.description = null;
		this.done = false;
	}
	/**
	 * Creates a new task
	 * @param story UserStory that task is tied to
	 * @param description description of task
	 */
	public Task(UserStory story, String description) {
		setStory(story);
		setDescription(description);
		this.done = false;
	}
	
	/**
	 * Sets the task's UserStory
	 * @param story story that task should be tied to
	 */
	public void setStory(UserStory story) {
		this.story = story;
	}
	/**
	 * Sets the task description
	 * @param taskDescription description of the task
	 */
	public void setDescription(String taskDescription){
		this.description = taskDescription;
	}
	/**
	 * @return task's UserStory
	 */
	public UserStory getStory() {
		return this.story;
	}
	
	/**
	 * @return task's description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @return task's done boolean
	 */
	public boolean isFinished() {
		return done;
	}
}
