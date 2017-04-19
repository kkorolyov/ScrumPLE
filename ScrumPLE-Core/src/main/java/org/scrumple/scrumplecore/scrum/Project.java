package org.scrumple.scrumplecore.scrum;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import dev.kkorolyov.sqlob.annotation.Transient;

/**
 * Base Scrum entity.
 */
public class Project {
	private String name;
	private String description;
	@Transient
	@JsonProperty(access = Access.WRITE_ONLY)
	private User owner;

	public Project(){}
	/**
	 * Constructs a new project without an owner.
	 * @param name project name
	 * @param description project description
	 */
	public Project(String name, String description) {
		this(name, description, null);
	}
	/**
	 * Constructs a new project.
	 * @param name project name
	 * @param description project description
	 * @param owner project owner
	 */
	public Project(String name, String description, User owner) {
		setName(name);
		setDescription(description);
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

	/** @return project owner */
	public User getOwner() {
		return owner;
	}
	/** @param owner new project owner */
	public void setOwner(User owner) {
		if (owner != null)
			owner.setRole("owner");

		this.owner = owner;
	}

	@Override
	public String toString() {
		return name + ", " + description;
	}
}
