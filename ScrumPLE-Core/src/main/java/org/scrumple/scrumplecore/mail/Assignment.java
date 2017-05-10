package org.scrumple.scrumplecore.mail;

import java.util.Random;

import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.Task;
import org.scrumple.scrumplecore.scrum.User;

/**
 * An assignment to a task.
 */
public class Assignment {
	private static final String[] suffixes = {"dizzle", "dazzle", "G", "dawg", "man", "bro", "dude", "duddete", "bby"};
	private static final Random random = new Random();

	private final Project project;
	private final User user;
	private final Task task;

	/**
	 * Constructs a new assignment.
	 * @param project project with assignment
	 * @param user user assigned to
	 * @param task task assigned
	 */
	public Assignment(Project project, User user, Task task) {
		this.project = project;
		this.user = user;
		this.task = task;
	}

	/**
	 * Sends this assignment.
	 */
	public void send() {
		Mailer.send(user.getCredentials().getHandle(), "Received assignment in " + project, "Whut up " + name(user) + "?, " + System.lineSeparator() + "You have been assigned to '" + task.getDescription() + "' in " + project.getName());
	}

	private static String name(User user) {
		return user.getDisplayName() + "-" + suffixes[random.nextInt(suffixes.length)];
	}
}
