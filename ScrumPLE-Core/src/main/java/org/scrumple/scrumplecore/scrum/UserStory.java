package org.scrumple.scrumplecore.scrum;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserStory {
	private String story;
	private int storyPoint;
	private Sprint sprint;
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
	
	public void setPoint(int storyPoint) {
		this.storyPoint = storyPoint;
	}
	
	public int getSprint() {
		if(sprint == null){
			return -1;
		}
		return sprint.getNumber();
	}
	
	public void setSprint(Sprint sprint) {
		this.sprint = sprint;
		this.sprintNumber = sprint.getNumber();
	}
	
}
