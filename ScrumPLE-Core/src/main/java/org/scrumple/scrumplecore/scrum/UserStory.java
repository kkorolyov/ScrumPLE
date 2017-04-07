package org.scrumple.scrumplecore.scrum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonInclude.Include;

//@JsonInclude(Include.NON_NULL)
public class UserStory {
	private String story;
	private int storyPoint;
	@JsonIgnore
	private Sprint sprint;
	@JsonIgnore
	private int sprintNumber;
	
	public UserStory() {}
	
	public UserStory(String story, int storyPoint) {
		setStory(story);
		setStoryPoint(storyPoint) ;
	}
	
	public String getStory() {
		return story;
	}
	
	public void setStory(String story) {
		this.story = story;
	}
	
	public int getStoryPoint() {
		return storyPoint;
	}
	
	public void setStoryPoint(int storyPoint) {
		this.storyPoint = storyPoint;
	}
	
	@JsonIgnore
	public Sprint getSprint() {
		return sprint;
	}
	
	@JsonIgnore
	public void setSprint(Sprint sprint) {
		this.sprint = sprint;
		this.sprintNumber = sprint.getNumber();
	}
	
}
