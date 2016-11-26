package org.scrumple.scrumplecore.resource;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.SQLException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ResourceConfig;
import org.scrumple.scrumplecore.assets.Assets;
import org.scrumple.scrumplecore.auth.AuthenticationException;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

@SuppressWarnings("javadoc")
@ApplicationPath("rest")
public class Application extends ResourceConfig {
	// Dinky web.xml
	
	public Application(@Context ServletContext context) throws MalformedURLException {
		packages("org.scrumple.scrumplecore.resource");	// Load JAX-RS components
		
		File root = new File(context.getResource("WEB-INF/").getFile());
		Assets.init(root);
	}
	
	@WebListener
	public static class ThreadCleanupListener implements ServletContextListener {
		@Override
		public void contextDestroyed(ServletContextEvent arg0) {
			try {
				AbandonedConnectionCleanupThread.shutdown();	// MySQL connection lingers for some reason
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void contextInitialized(ServletContextEvent arg0) {
			// Ignore
		}
	}
	
	@Provider	
	public static class DebugMapper implements ExceptionMapper<Exception> {
		@Override
		public Response toResponse(Exception e) {
			e.printStackTrace();
			return buildExceptionResponse(500, e);
		}
	}
	@Provider
	public static class SQLExceptionMapper implements ExceptionMapper<SQLException> {
		@Override
		public Response toResponse(SQLException e) {
			return buildExceptionResponse(500, e);
		}
	}
	@Provider
	public static class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {
		@Override
		public Response toResponse(EntityNotFoundException e) {
			return buildExceptionResponse(404, e);
		}
	}
	@Provider
	public static class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {
		@Override
		public Response toResponse(AuthenticationException e) {
			return buildExceptionResponse(401, e);
		}
	}
	
	private static Response buildExceptionResponse(int errorCode, Throwable e) {
		return Response.status(errorCode).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}
}
