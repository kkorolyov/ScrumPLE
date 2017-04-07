package org.scrumple.scrumplecore.scrum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(Include.NON_NULL)
public class UserStory {
	private String story;
	private int storyPoint;
	private Sprint sprint;
	@JsonIgnore
	private int sprintNumber;
	
	public UserStory() {}
	
	@JsonCreator
	public UserStory(@JsonProperty("story") String story, @JsonProperty("storyPoint") int storyPoint) {
		setStory(story);
		setPoint(storyPoint) ;
	}
	
	public String getStory() {
		return story;
	}
	
	public void setStory(String story) {
		this.story = story;
	}
	
	public int getPoint() {
		return storyPoint;
	}
	
	public void setStoryPoint(int storyPoint) {
		this.storyPoint = storyPoint;
	}
	
	public Sprint getSprint() {
		return sprint;
	}
	
	public void setSprint(Sprint sprint) {
		this.sprint = sprint;
		this.sprintNumber = sprint.getNumber();
	}
	
}
