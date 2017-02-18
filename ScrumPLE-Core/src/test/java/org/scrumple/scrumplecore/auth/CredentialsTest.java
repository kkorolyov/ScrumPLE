package org.scrumple.scrumplecore.auth;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CredentialsTest {
	private Credentials credentials;

	@Test
	public void emptyCredentialsWork() {
		credentials = new Credentials("", "");

		assertEquals("", credentials.getHandle());
	}

	@Test
	public void emptyCredentialsEqual() {
		credentials = new Credentials("","");
		Credentials other = new Credentials("","");

		assertEquals(credentials, other);
	}
}
