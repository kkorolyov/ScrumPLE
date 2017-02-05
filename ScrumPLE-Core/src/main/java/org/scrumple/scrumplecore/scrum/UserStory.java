package org.scrumple.scrumplecore.scrum;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserStory {
	@XmlElement
	private String story;
	
	public UserStory() {}
	
	public UserStory(String story) {
		this.story = story;
	}
	
	public String getStory() {
		return story;
	}

}
