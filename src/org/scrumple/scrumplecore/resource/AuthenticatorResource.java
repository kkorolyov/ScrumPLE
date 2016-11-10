package org.scrumple.scrumplecore.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("auth")
public class AuthenticatorResource {
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String authenticate(@QueryParam("handle") String user, @QueryParam("pass") String password) {
		return user + " = " + password;
	}
}
