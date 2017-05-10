package org.scrumple.scrumplecore.assets;

import java.nio.file.Path;
import java.nio.file.Paths;

import dev.kkorolyov.simplelogs.Level;
import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.format.Formatters;
import dev.kkorolyov.simpleprops.Properties;

/**
 * Global access to all external assets.
 */
public final class Assets {
	public static final String DB_HOST = "databaseHost";
	public static final String DB_PORT = "databasePort";
	public static final String DB_USER = "databaseUser";
	public static final String DB_PASSWORD = "databasePassword";

	public static final String SYSTEM_DB = "systemDatabase";

	public static final String MAILER_USERNAME = "mailerUsername";
	public static final String MAILER_PASSWORD = "mailerPassword";

	private static final Logger log = Logger.getLogger(Level.DEBUG, Formatters.simple());
	private static final String LOG_PROPS = "logProps";
	private static final Properties config = new Properties();

	/**
	 * @param key property identifier
	 * @return value of property defined by {@code key}
	 */
	public static String get(String key) {
		return config.get(key);
	}

	/**
	 * Applies properties to the global configuration, overwriting preexisiting properties of the same name.
	 * @param properties all properties to apply
	 */
	public static void applyConfig(Properties properties) {
		config.put(properties, true);

		applyLogProps();

		log.info("Config was updated; current config: " + config);
	}
	private static void applyLogProps() {
		String logProps = config.get(LOG_PROPS);

		if (logProps != null) {
			Path logPropsPath = Paths.get(logProps);

			Logger.applyProps(logPropsPath);
			log.info("Applied logging properties from: " + logPropsPath);
		}
	}
}
