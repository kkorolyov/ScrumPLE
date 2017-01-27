package org.scrumple.scrumplecore.applications;

import dev.kkorolyov.sqlob.annotation.Transient;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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
