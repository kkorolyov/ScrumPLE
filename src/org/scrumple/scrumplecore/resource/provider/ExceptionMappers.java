package org.scrumple.scrumplecore.resource.provider;

import java.sql.SQLException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.scrumple.scrumplecore.auth.AuthenticationException;

/**
 * Contains all exception mappers.
 */
@SuppressWarnings("synthetic-access")
public final class ExceptionMappers {
	private ExceptionMappers() {/* Not instantiable */}

	/*@Provider
	public static class DebugMapper implements ExceptionMapper<Exception> {
		@Override
		public Response toResponse(Exception e) {
			e.printStackTrace();
			return buildExceptionResponse(500, e);
		}
	}*/
	
	@Provider
	private static class SQLExceptionMapper implements ExceptionMapper<SQLException> {
		@Override
		public Response toResponse(SQLException e) {
			return buildExceptionResponse(500, e);
		}
	}
	
	@Provider
	private static class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {
		@Override
		public Response toResponse(EntityNotFoundException e) {
			return buildExceptionResponse(404, e);
		}
	}
	@Provider
	private static class EntityExistsExceptionMapper implements ExceptionMapper<EntityExistsException> {
		@Override
		public Response toResponse(EntityExistsException e) {
			return buildExceptionResponse(409, e);
		}
	}
	
	@Provider
	private static class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {
		@Override
		public Response toResponse(AuthenticationException e) {
			return buildExceptionResponse(401, e);
		}
	}
	
	private static Response buildExceptionResponse(int errorCode, Throwable e) {
		return Response.status(errorCode).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}
}
