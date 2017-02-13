package org.scrumple.scrumplecore.scrum;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base Scrum entity.
 */
public class Project {
	private String name,
			description;
	private boolean visible;

	public Project(){}

	/**
	 * Constructs a new project.
	 * @param name project name
	 * @param description project description
	 * @param visible public visibility
	 */
	public Project(String name, String description, boolean visible) {
		setName(name);
		setDescription(description);
		setVisible(visible);
	}

	/** @return project name */
	public String getName() {
		return this.name;
	}
	/** @param name new project name */
	public void setName(String name) {
		this.name = name;
	}

	/** @return project description */
	public String getDescription() {
		return this.description;
	}
	/** @param description new project description */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/** @return {@code true} if this project is private */
	@JsonProperty("visible")
	public boolean isVisible() {
		return visible;
	}
	/**
	 * Sets this project's {@code private} status.
	 * @param visible whether this project is publicly visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	@Override
	public String toString() {
		return name + ", " + description + ", " + visible;
	}
}
