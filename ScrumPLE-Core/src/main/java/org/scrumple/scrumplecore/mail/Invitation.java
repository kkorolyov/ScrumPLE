package org.scrumple.scrumplecore.mail;

import java.util.Properties;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.kkorolyov.simplelogs.Level;
import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.format.Formatters;

/**
 * An invitation to join a project.
 */
public class Invitation {
	private static final String USERNAME = "scrumple.cde@gmail.com";
	private static final String PASSWORD = "scrumplepass";
	private static final Logger log = Logger.getLogger(Level.DEBUG, Formatters.simple());

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
		Properties props = new Properties();
		props.put("mail.smtp.host" , "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		Session s = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USERNAME, PASSWORD);
			}
		});

		try {
			Message msg = new MimeMessage(s);
			msg.setFrom();
			msg.setRecipients(RecipientType.TO, InternetAddress.parse(email));
			msg.setSubject("Invitation to collaborate on " + project);
			msg.setText(message + System.lineSeparator()
									+ url);

			Transport.send(msg);
			log.info("Sent invitation: " + this);
		} catch (MessagingException e) {
			log.exception(e);
		}
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
