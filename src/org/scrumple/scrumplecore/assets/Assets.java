package org.scrumple.scrumplecore.assets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;
import dev.kkorolyov.simpleprops.Properties;

/**
 * Global access to all external assets.
 */
public class Assets {
	private static final Logger log = Logger.getLogger(Assets.class.getName(), Level.DEBUG, new PrintWriter(System.err));
	
	/**
	 * Initializes Assets.
	 */
	@SuppressWarnings("synthetic-access")
	public static void init(File config, File logging) {
		Config.init(config);
		LogFiles.init(logging);
		
		log.debug("Initialized Assets");
	}
	
	private static void save(Properties props) {
		try {
			props.saveFile(true);
		} catch (IOException e) {
			log.exception(e);
		}
	}
	
	@SuppressWarnings({"unused", "synthetic-access"})
	private static class Defaults {	// Creates config file defaults
		private static final String SYSTEM_DB = "ScrumPLE";
		
		static Properties buildDefaultsForClass(Class<?> c) {	// Builds defaults for all public fields in class
			Properties defaults = new Properties();
			
			for (Field field : c.getFields()) {
				String 	key = "",
								value = "";	// Default to empty
				try {
					key = (String) field.get(null);
					value = (String) Defaults.class.getDeclaredField(field.getName()).get(null);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.exception(e);
				} catch (NoSuchFieldException e) {
					// Should only happen if a default not specified
				}
				defaults.put(key, value);
			}
			return defaults;
		}
	}
	
	/**
	 * Provides access to application configuration parameters.
	 */
	@SuppressWarnings("javadoc")
	public static class Config {
		public static final String 	DB_HOST = "databaseHost",
																DB_PORT = "databasePort",
																DB_USER = "databaseUser",
																DB_PASSWORD = "databasePassword";
		public static final String 	SYSTEM_DB = "systemDatabase";
		
		private static Properties props;
		
		@SuppressWarnings("synthetic-access")
		private static void init(File file) {
			props = new Properties(file, Defaults.buildDefaultsForClass(Config.class));
			save(props);
			
			log.debug("Loaded SQL properties");
		}
		
		/** @return value mapped to {@code key}, or {@code null} if no such key */
		public static String get(String key) {
			return props.get(key);
		}
		/** @return file mapped to {@code key}, or {@code null} if no such key */
		public static File getFile(String key) {
			String fileName = props.get(key);
			return fileName != null ? new File(fileName) : null;
		}
		
		/** @return configured database server address */
		public static String getServer() {
			return get(DB_HOST);
		}
		/** @return configured database server port */
		public static int getPort() {
			return Integer.parseInt(get(DB_PORT));
		}
		/** @return configured database user */
		public static String getUser() {
			return get(DB_USER);
		}
		/** @return configured database password */
		public static String getPassword() {
			return get(DB_PASSWORD);
		}
	}
	
	@SuppressWarnings("synthetic-access")
	private static class LogFiles {
		private static final String CONFIG_DELIMITER = Pattern.quote(",");
		private static final String GLOBAL_LOGGER = "GLOBAL";
		private static final Level DEFAULT_LEVEL = Level.INFO;
		
		private static Properties props;
		
		private static void init(File file) {
			props = new Properties(file, Defaults.buildDefaultsForClass(LogFiles.class));
			save(props);
			
			applyLoggers();
			
			log.debug("Loaded LogFiles");
		}
		
		private static void applyLoggers() {
			for (String logger : props.keys()) {
				try {
					String[] config = props.get(logger).split(CONFIG_DELIMITER);
					File file = new File(config[0].trim());
					
					if (!file.isFile()) {	// Create filepath if needed
						File parent = file.getParentFile();
						if (parent != null && !parent.exists())
							parent.mkdirs();
					}
					Level level = config.length > 1 ? Level.valueOf(config[1].trim()) : DEFAULT_LEVEL;
					PrintWriter writer = new PrintWriter(file);
					
					Logger.getLogger((logger.equals(GLOBAL_LOGGER) ? "" : logger), level, writer);
					
					log.debug("Loaded Logger: name=" + logger + ", file=" + file + ", level=" + level);
				} catch (FileNotFoundException e) {	// Should not happen
					log.exception(e);
				}
			}
		}
	}
}
