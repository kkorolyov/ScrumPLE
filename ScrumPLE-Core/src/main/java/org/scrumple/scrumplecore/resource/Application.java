package org.scrumple.scrumplecore.resource;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.server.ResourceConfig;
import org.scrumple.scrumplecore.assets.Assets;

import dev.kkorolyov.simpleprops.Properties;

@SuppressWarnings("javadoc")
public class Application extends ResourceConfig {
	public Application(@Context ServletContext context) throws MalformedURLException, URISyntaxException {
		Assets.applyConfig(parseInitProps(context));
	}
	private static Properties parseInitProps(ServletContext context) {
		Properties props = new Properties();

		Enumeration<String> names = context.getInitParameterNames();
		while (names.hasMoreElements()) {
			String 	key = names.nextElement(),
							value = context.getInitParameter(key);
			props.put(key, value);
		}
		return props;
	}
}
