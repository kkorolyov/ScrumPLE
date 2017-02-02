package org.scrumple.scrumplecore.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Project {
	@JsonProperty
	private String name;
	@JsonProperty
	private String description;
	@JsonProperty
	private boolean isPrivate;

	public Project(){}
	public Project(String name, String description, boolean isPrivate) {
		setName(name);
		setDescription(description);
		setPrivate(isPrivate);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	/** @return {@code true} if this project is private */
	@JsonIgnore
	public boolean isPrivate() {
		return isPrivate;
	}
	/**
	 * Sets this project's {@code private} status.
	 * @param isPrivate whether this project is private
	 */
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	
	@Override
	public String toString() {
		return name + ", " + description + ", " + isPrivate;
	}
}
