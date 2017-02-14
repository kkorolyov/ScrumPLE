package org.scrumple.scrumplecore.scrum;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kkorolyov.sqlob.annotation.Transient;

/**
 * Base Scrum entity.
 */
public class Project {
	private String name,
			description;
	private boolean visible;
	@Transient
	private User owner;

	public Project(){}
	/**
	 * Constructs a new project without an owner.
	 * @param name project name
	 * @param description project description
	 * @param visible public visibility
	 */
	public Project(String name, String description, boolean visible) {
		this(name, description, visible, null);
	}
	/**
	 * Constructs a new project.
	 * @param name project name
	 * @param description project description
	 * @param visible public visibility
	 * @param owner project owner
	 */
	public Project(String name, String description, boolean visible, User owner) {
		setName(name);
		setDescription(description);
		setVisible(visible);
		setOwner(owner);
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

	/** @return project owner */
	@JsonIgnore
	public User getOwner() {
		return owner;
	}
	/** @param owner new project owner */
	@JsonProperty
	public void setOwner(User owner) {
		if (owner != null)
			owner.setRole("owner");

		this.owner = owner;
	}

	@Override
	public String toString() {
		return name + ", " + description + ", " + visible;
	}
}
