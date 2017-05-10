package org.scrumple.scrumplecore.mail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An invitation to join a project.
 */
public class Invitation {
	private final String email;
	private final String message;
	private final String project;
	private final String url;

	/**
	 * Constructs a new invitation.
	 * @param email recipient email
	 * @param message invitation message
	 * @param project project name
	 * @param url invitation URL
	 */
	@JsonCreator
	public Invitation(@JsonProperty("email") String email, @JsonProperty("message") String message, @JsonProperty("project") String project, @JsonProperty("url") String url) {
		this.email = email;
		this.message = message;
		this.project = project;
		this.url = url;
	}

	/**
	 * Sends this invitation.
	 */
	public void send() {
		Mailer.send(email, "Invitation to collaborate on " + project, message + System.lineSeparator() + url);
	}

	@Override
	public String toString() {
		return "Invitation{" +
					 "email='" + email + '\'' +
					 ", message='" + message + '\'' +
					 ", project='" + project + '\'' +
					 ", url='" + url + '\'' +
					 '}';
	}
}
