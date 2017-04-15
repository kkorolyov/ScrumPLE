package org.scrumple.scrumplecore.mail;

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
	public Invitation(String email, String message, String url) {
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
