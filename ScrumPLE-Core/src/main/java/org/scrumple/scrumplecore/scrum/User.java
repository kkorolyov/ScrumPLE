package org.scrumple.scrumplecore.scrum;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.scrumple.scrumplecore.auth.Credentials;

/**
 * A user and member of a project team.
 */
public class User {
	private Credentials credentials;
	private String displayName,
			role;
	
	public User(){}

	/**
	 * Constructs a new user with display name matching credentials handle and a {@code null} role.
	 * @param credentials authorization credentials
	 */
	public User(Credentials credentials) {
		this(credentials, null);
	}
	/**
	 * Constructs a new user with display name matching credentials handle.
	 * @param credentials authorization credentials
	 * @param role user role
	 */
	public User(Credentials credentials, String role) {
		this(credentials, credentials.getHandle(), role);
	}
	/**
	 * Constructs a new user.
	 * @param credentials authorization credentials
	 * @param displayName public display name
	 * @param role user role
	 */
	public User(Credentials credentials, String displayName, String role) {
		this.credentials = credentials;
		this.displayName = displayName;
		this.role = role;
	}

	/** @return authorization credentials */
	public Credentials getCredentials() {
		return credentials;
	}
	/** @param credentials new authorization credentials */
	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	/** @return displayed name */
	public String getDisplayName() {
		return displayName;
	}
	/** @param displayName new display name */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/** @return role */
	public String getRole() {
		return this.role;
	}
	/** @param roleIDX new role */
	public void setRole(String roleIDX) {
		this.role = roleIDX;
	}
}
