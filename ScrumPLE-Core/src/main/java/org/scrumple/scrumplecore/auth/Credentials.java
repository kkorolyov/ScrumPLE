package org.scrumple.scrumplecore.auth;

import java.util.Base64;

/**
 * A handle-password pair encoded in Base64.
 */
public class Credentials {
	private String encoded;

	public Credentials(){}
	/**
	 * Constructs new credentials from an unencoded handle and password.
	 * @param handle credentials handle
	 * @param password credentials password
	 */
	public Credentials(String handle, String password) {
		setCredentials(handle, password);
	}

	/**
	 * Constructs new credentials from a string which, when Base64-decoded, matches the form: {@code handle:password}.
	 * @param encoded encoded credentials
	 */
	public Credentials(String encoded) {
		this.encoded = encoded;
	}

	private String[] decode() {
		byte[] decodedBytes = Base64.getDecoder().decode(encoded);
		return new String(decodedBytes).split(":");
	}

	/** @return handle or login name */
	public String getHandle() {
		return decode()[0];
	}
	/** @return password */
	public String getPassword() {
		return decode()[1];
	}

	/**
	 * Sets credentials.
	 * @param handle credentials handle or login name
	 * @param password credentials password
	 */
	public void setCredentials(String handle, String password) {
		String credentials = handle + ":" + password;
		byte[] encodedBytes = Base64.getEncoder().encode(credentials.getBytes());
		encoded = new String(encodedBytes);
	}

	@Override
	public String toString() {
		return getHandle();
	}
}
