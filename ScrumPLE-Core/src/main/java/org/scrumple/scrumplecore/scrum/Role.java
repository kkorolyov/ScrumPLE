package org.scrumple.scrumplecore.scrum;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Role {
	@JsonProperty
	private String value;
	
	public Role(){}
	public Role(String value) {
		this.value = value;
	}
}
