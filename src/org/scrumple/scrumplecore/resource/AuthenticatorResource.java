package org.scrumple.scrumplecore.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.scrumple.scrumplecore.applications.User;
import org.scrumple.scrumplecore.auth.AuthenticationException;
import org.scrumple.scrumplecore.service.ServiceLoader;

@Path("auth")
public class AuthenticatorResource {
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String authenticate(@QueryParam("handle") String handle, @QueryParam("pass") String password) {
		User user;
		try {
			user = ServiceLoader.getAuthenticator().get(handle, password);
		} catch (AuthenticationException e) {
			return "OOPSIE Happened";
		}
		return user.getRole().toString();
	}
}
