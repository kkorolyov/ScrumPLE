package org.scrumple.scrumplecore.scrum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.UUID;

/**
 * Represents a task
 */
@JsonInclude(Include.NON_NULL)
public class Task {
	private String description;
	private UserStory story;
	private UUID user;
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
	 * Sets the user for the task.
	 */
	public void setUser(String user) {
		this.user = UUID.fromString(user);
	}

	/**
	 * @return the task's user.
	 */
	public UUID getUser() {
		return this.user;
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
	public boolean isDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;

	}
}
