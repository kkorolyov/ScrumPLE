package org.scrumple.scrumplecore.mail;

import static org.scrumple.scrumplecore.assets.Assets.MAILER_PASSWORD;
import static org.scrumple.scrumplecore.assets.Assets.MAILER_USERNAME;

import java.util.Properties;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.scrumple.scrumplecore.assets.Assets;

import dev.kkorolyov.simplelogs.Level;
import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.format.Formatters;

/**
 * Mails email.
 */
class Mailer {
	private static final Logger log = Logger.getLogger(Level.DEBUG, Formatters.simple());

	static void send(String email, String subject, String message) {
		Properties props = new Properties();
		props.put("mail.smtp.host" , "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		Session s = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Assets.get(MAILER_USERNAME), Assets.get(MAILER_PASSWORD));
			}
		});

		try {
			Message msg = new MimeMessage(s);
			msg.setFrom();
			msg.setRecipients(RecipientType.TO, InternetAddress.parse(email));
			msg.setSubject(subject);
			msg.setText(message);

			Transport.send(msg);
			log.info("Sent invitation: {}", msg);
		} catch (MessagingException e) {
			log.exception(e);
		}
	}
}
