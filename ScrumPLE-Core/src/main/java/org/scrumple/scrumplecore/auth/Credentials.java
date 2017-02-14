package org.scrumple.scrumplecore.auth;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Credentials.
 */
public class Credentials {
	private String handle;
	private String password;

	public Credentials(){}
	/**
	 * Constructs new credentials from a concatenated handle-password pair.
	 * @param concat64 handle and password concatenated in the form {@code [handle]:[password]} and encoded in Base64
	 */
	public Credentials(String concat64) {
		Base64.Decoder decoder = Base64.getDecoder();

		String decoded = new String(decoder.decode(concat64));
		String[] split = decoded.split(":");

		setHandle(split[0]);
		setPassword(split[1]);
	}
	/**
	 * Constructs new credentials from plain handle and password.
	 * @param handle credentials handle
	 * @param password credentials password
	 */
	public Credentials(String handle, String password) {
		setHandle(handle);
		setPassword(password);
	}

	/** @return credentials handle */
	public String getHandle() {
		return handle;
	}
	/** @param handle new handle */
	public void setHandle(String handle) {
		this.handle = handle;
	}

	/** @param password new password */
	public void setPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] passwordBytes = md.digest(password.getBytes("UTF-8"));

			this.password = DatatypeConverter.printHexBinary(passwordBytes);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			this.password = password;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(handle, password);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj.getClass() != Credentials.class) return false;

		Credentials other = (Credentials) obj;
		return Objects.equals(handle, other.handle)
				&& Objects.equals(password, other.password);
	}

	@Override
	public String toString() {
		return handle;
	}
}
