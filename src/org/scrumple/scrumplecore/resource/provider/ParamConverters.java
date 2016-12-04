package org.scrumple.scrumplecore.resource.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.UUID;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

/**
 * Defines all param converters. 
 */
@Provider
public class ParamConverters implements ParamConverterProvider {
	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
		if (rawType.isAssignableFrom(UUID.class)) {
			return new ParamConverter<T>() {
				@Override
				public T fromString(String value) {
					return rawType.cast(UUID.fromString(value));
				}
				@Override
				public String toString(T value) {
					return value.toString();
				}
			};
		}
		return null;
	}
}
