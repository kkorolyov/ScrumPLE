package org.scrumple.scrumplecore.scrum;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Representation of a User Story.
 */
@JsonInclude(Include.NON_NULL)
public class UserStory {
	private String story;
	private int storyPoint;
	private Sprint sprint;
	@JsonIgnore
	private int sprintNumber;
	
	public UserStory() {}
	
	/**
	 * Constructs a new UserStory
	 * @param story name of the User Story
	 * @param storyPoint value of the storyPoint assigned to the user story
	 */
	public UserStory(String story, int storyPoint) {
		setStory(story);
		setStoryPoint(storyPoint) ;
	}
	/**
	 * @return story name
	 */
	public String getStory() {
		return story;
	}
	/**
	 * Set story name
	 * @param story new story
	 */
	public void setStory(String story) {
		this.story = story;
	}
	/**
	 * @return storyPoint
	 */
	public int getStoryPoint() {
		return storyPoint;
	}
	/**
	 * set User Story's storyPoint
	 * @param storyPoint new storyPoint
	 */
	public void setStoryPoint(int storyPoint) {
		this.storyPoint = storyPoint;
	}
	/**
	 * @return User Story's sprint
	 */
	public Sprint getSprint() {
		return sprint;
	}
	/**
	 * set User Story's sprint
	 * @param sprint new sprint
	 */
	public void setSprint(Sprint sprint) {
		this.sprint = sprint;
		this.sprintNumber = sprint.getSprintNumber();
	}
	
}
