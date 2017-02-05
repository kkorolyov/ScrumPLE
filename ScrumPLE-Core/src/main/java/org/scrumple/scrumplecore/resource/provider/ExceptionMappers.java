package org.scrumple.scrumplecore.resource.provider;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;
import org.scrumple.scrumplecore.auth.AuthenticationException;
import org.scrumple.scrumplecore.auth.AuthorizationException;
import org.scrumple.scrumplecore.database.DataAccessException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Contains all exception mappers.
 */
@SuppressWarnings("synthetic-access")
public final class ExceptionMappers {
	private static final Logger log = Logger.getLogger(ExceptionMappers.class.getName(), Level.DEBUG);

	private ExceptionMappers() {/* Not instantiable */}

	@Provider
	private static class IllegalArgumentExceptionMapper extends ExceptionMapperLogger<IllegalArgumentException> {
		public IllegalArgumentExceptionMapper() {
			super(400);
		}
	}

	@Provider
	private static class AuthorizationExceptionMapper extends ExceptionMapperLogger<AuthorizationException> {
		public AuthorizationExceptionMapper() {
			super(401);
		}

	}

	@Provider
	private static class EntityNotFoundExceptionMapper extends ExceptionMapperLogger<EntityNotFoundException> {
		public EntityNotFoundExceptionMapper() {
			super(404);
		}
	}
	@Provider
	private static class EntityExistsExceptionMapper extends ExceptionMapperLogger<EntityExistsException> {
		public EntityExistsExceptionMapper() {
			super(409);
		}

	}
	@Provider
	private static class DataAccessExceptionMapper extends ExceptionMapperLogger<DataAccessException> {
		public DataAccessExceptionMapper() {
			super(500);
		}

	}

	private static class ExceptionMapperLogger<T extends Throwable> implements ExceptionMapper<T> {
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
