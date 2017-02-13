package org.scrumple.scrumplecore.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Base64;

/**
 * A handle-password pair encoded in Base64.
 */
public class Credentials {
	private String value;
	//private String handle,	// TODO Use these instead
	//		hashedPass;

	public Credentials(){}
	/**
	 * Constructs new credentials from an unencoded handle and password.
	 * @param handle credentials handle
	 * @param password credentials password
	 */
	@JsonCreator
	public Credentials(@JsonProperty String handle, @JsonProperty String password) {
		setCredentials(handle, password);
	}

	/**
	 * Constructs new credentials from a string which, when Base64-decoded, matches the form: {@code handle:password}.
	 * @param value value credentials
	 */
	public Credentials(String value) {
		this.value = value;
	}

	private String[] decode() {
		byte[] decodedBytes = Base64.getDecoder().decode(value);
		return new String(decodedBytes).split(":");
	}

	/** @return handle or login name */
	public String getHandle() {
		return decode()[0];
	}
	/** @return password */
	@JsonIgnore
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
		value = new String(encodedBytes);
	}

	@Override
	public String toString() {
		return getHandle();
	}
}
