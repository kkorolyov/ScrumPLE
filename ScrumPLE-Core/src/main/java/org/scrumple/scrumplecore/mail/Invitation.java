package org.scrumple.scrumplecore.mail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An invitation to join a project.
 */
public class Invitation {
	private final String email;
	private final String message;
	private final String url;

	/**
	 * Constructs a new invitation.
	 * @param email recipient email
	 * @param message invitation message
	 * @param url invitation URL
	 */
	@JsonCreator
	public Invitation(@JsonProperty("email") String email, @JsonProperty("message") String message, @JsonProperty("url") String url) {
		this.email = email;
		this.message = message;
		this.url = url;
	}

	/**
	 * Sends this invitation.
	 */
	public void send() {
		// TODO
	}
}
