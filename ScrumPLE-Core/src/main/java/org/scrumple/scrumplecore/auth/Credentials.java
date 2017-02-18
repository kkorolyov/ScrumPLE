package org.scrumple.scrumplecore.auth;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.DatatypeConverter;

/**
 * Credentials.
 */
public class Credentials {
	/** Represents empty credentials */
	public static final Credentials none = new Credentials("", "");

	private String handle;
	private String password;

	/**
	 * Parses credentials from HTTP headers.
	 * @param headers headers to parse credentials from
	 */
	public static Credentials fromHeaders(HttpHeaders headers) {
		List<String> authHeaders = headers == null ? null : headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
		String concat64 = (authHeaders == null || authHeaders.isEmpty()) ? null : authHeaders.iterator().next().replaceFirst("^.*?\\s+", "");	// Remove all before space
		if (concat64 == null) return none;

		Base64.Decoder decoder = Base64.getDecoder();

		String decoded = new String(decoder.decode(concat64));
		String[] split = decoded.split(":");

		return new Credentials(split.length < 1 ? none.handle : split[0], split.length < 2 ? none.password : split[1]);
	}

	public Credentials(){}
	/**
	 * Constructs new credentials.
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
