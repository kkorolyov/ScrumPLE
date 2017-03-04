package org.scrumple.scrumplecore.scrum;

import java.util.ArrayList;

public class UserStory {
	private String story;
	private int storyPoint;
	private Sprint sprint;
	
	public UserStory() {}
	
	public UserStory(String story, int storyPoint) {
		this.story = story;
		this.storyPoint = storyPoint;
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
	
	public void setPoint(int newPoint) {
		this.storyPoint = newPoint;
	}
	
	public int getSprint() {
		return sprint.getNumber();
	}
	
	public void setSprint(Sprint sprint) {
		this.sprint = sprint;
	}
	
}
