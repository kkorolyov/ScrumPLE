package org.scrumple.scrumplecore.resource;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.server.ResourceConfig;
import org.scrumple.scrumplecore.assets.Assets;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

@SuppressWarnings("javadoc")
@ApplicationPath("rest")
public class Application extends ResourceConfig {
	// Dinky web.xml
	
	public Application(@Context ServletContext context) throws MalformedURLException, URISyntaxException {
		packages("org.scrumple.scrumplecore.resource");	// Load JAX-RS components
		
		Path root = Paths.get(context.getResource("WEB-INF/").toURI());
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
}
