package org.scrumple.scrumplecore.resource.provider;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ContainerException;
import org.scrumple.scrumplecore.auth.AuthenticationException;
import org.scrumple.scrumplecore.auth.AuthorizationException;
import org.scrumple.scrumplecore.auth.SessionExpiredException;
import org.scrumple.scrumplecore.database.DataAccessException;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;

/**
 * Contains all exception mappers.
 */
final class ExceptionMappers {
	private ExceptionMappers() {/* Not instantiable */}

	@Provider
	static class DebugExceptionMapper extends ExceptionMapperLogger<Exception> {
		DebugExceptionMapper() {
			super(500);
		}
	}

	@Provider
	static class IllegalArgumentExceptionMapper extends ExceptionMapperLogger<IllegalArgumentException> {
		IllegalArgumentExceptionMapper() {
			super(400);
		}
	}

	@Provider
	static class AuthenticationExceptionMapper extends ExceptionMapperLogger<AuthenticationException> {
		AuthenticationExceptionMapper() {
			super(401);
		}
	}
	@Provider
	static class AuthorizationExceptionMapper extends ExceptionMapperLogger<AuthorizationException> {
		AuthorizationExceptionMapper() {
			super(401);
		}
	}
	@Provider
	static class SessionExpiredExceptionMapper extends ExceptionMapperLogger<SessionExpiredException> {
		SessionExpiredExceptionMapper() {
			super(401);
		}
	}

	@Provider
	static class EntityNotFoundExceptionMapper extends ExceptionMapperLogger<EntityNotFoundException> {
		EntityNotFoundExceptionMapper() {
			super(404);
		}
	}
	@Provider
	static class EntityExistsExceptionMapper extends ExceptionMapperLogger<EntityExistsException> {
		EntityExistsExceptionMapper() {
			super(409);
		}

	}
	@Provider
	static class DataAccessExceptionMapper extends ExceptionMapperLogger<DataAccessException> {
		DataAccessExceptionMapper() {
			super(500);
		}
	}

	@Provider
	static class ContainerExceptionMapper extends ExceptionMapperLogger<ContainerException> {
		ContainerExceptionMapper() {
			super(500);
		}
	}

	static class ExceptionMapperLogger<T extends Throwable> implements ExceptionMapper<T> {
		private static final Logger log = Logger.getLogger(ExceptionMappers.class.getName(), Level.DEBUG);

		private final int errorCode;

		ExceptionMapperLogger(int errorCode) {
			this.errorCode = errorCode;
		}

		@Override
		public Response toResponse(T e) {
			log.exception((Exception) e, Level.WARNING);
			return Response.status(errorCode).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
	}
}
