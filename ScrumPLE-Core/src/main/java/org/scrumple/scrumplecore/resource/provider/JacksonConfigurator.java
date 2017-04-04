package org.scrumple.scrumplecore.resource.provider;

import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides default Jackson configuration.
 */
public class JacksonConfigurator implements ContextResolver<ObjectMapper> {
	private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);	// Globally ignore unknown properties

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}
}
