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
	public static void init() {
		ConfigFiles.init();
		LogFiles.init();
		Sql.init();
		
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
		public static final String	CONFIG_FILES_CONFIG = "config/configs.ini",	// ConfigFiles defaults
																LOGGERS_CONFIG = "config/logging.ini",
																SQL_CONFIG = "config/sql.ini";
		public static final String 	INIT_DATABASE_SCRIPT = "sql/init-database.sql",	// SQL defaults
																CREATE_ROLES_SCRIPT = "sql/create-default-roles.sql",
																PARAMETER_MARKER = "?";
		
		private static Properties propFiles() {
			log.debug("Building defaults for PropFiles...");

			return buildDefaultsForClass(ConfigFiles.class);
		}
		private static Properties logFiles() {
			log.debug("Building defaults for LogFiles...");
			
			return buildDefaultsForClass(LogFiles.class);
		}
		private static Properties sql() {
			log.debug("Building defaults for sql...");

			return buildDefaultsForClass(Sql.class);
		}
		
		private static Properties buildDefaultsForClass(Class<?> c) {	// Builds defaults for all public field names in class
			Field[] fields = c.getFields();
			String[] fieldNames = new String[fields.length];
			
			int counter = 0;
			for (Field field : fields)
				fieldNames[counter++] = field.getName();
			
			return buildDefaults(fieldNames);
		}
		private static Properties buildDefaults(String... keys) {
			Properties props = new Properties();
			
			for (String key : keys)
				loadDefault(props, key);
			
			return props;
		}
		private static void loadDefault(Properties props, String key) {
			String defaultValue = "";
			
			try {
				defaultValue = (String) Defaults.class.getField(key).get(null);	// Assume default value fields match keys
			} catch (NoSuchFieldException e) {
				// Empty value
			} catch (SecurityException | IllegalAccessException e) {	// These should be logged
				log.exception(e);
			}
			props.put(key, defaultValue);
		}
	}
	
	@SuppressWarnings("synthetic-access")
	private static class ConfigFiles {	// Returns config file locations
		public static final String 	LOGGERS_CONFIG = "LOGGERS_CONFIG",
																SQL_CONFIG = "SQL_CONFIG";
		
		private static Properties props;
		
		private static void init() {
			props = new Properties(new File(Defaults.CONFIG_FILES_CONFIG), Defaults.propFiles());
			save(props);
			
			log.debug("Loaded PropFiles properties");
		}
		
		public static File get(String key) {
			String fileName = props.get(key);
			
			return fileName != null ? new File(fileName) : null;
		}
	}
	
	@SuppressWarnings("synthetic-access")
	private static class LogFiles {
		private static final String CONFIG_DELIMITER = Pattern.quote(",");
		private static final String GLOBAL_LOGGER = "GLOBAL";
		private static final Level DEFAULT_LEVEL = Level.INFO;
		
		private static Properties props;
		
		private static void init() {
			props = new Properties(ConfigFiles.get(ConfigFiles.LOGGERS_CONFIG), Defaults.logFiles());
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
					Level level = config.length > 1 ? parseLevel(config[1].trim()) : DEFAULT_LEVEL;
					PrintWriter writer = new PrintWriter(file);
					
					Logger.getLogger(logger.equals(GLOBAL_LOGGER) ? "" : logger, level, writer);
					
					log.debug("Loaded Logger: name=" + logger + ", file=" + file + ", level=" + level);
				} catch (FileNotFoundException e) {	// Should not happen
					log.exception(e);
				}
			}
		}
		
		private static Level parseLevel(String levelName) {
			if (levelName != null) {
				for (Level level : Level.values()) {
					if (level.name().equals(levelName.toUpperCase()))
						return level;
				}
			}
			return null;
		}
	}
	
	/**
	 * Returns SQL properties and statements.
	 */
	@SuppressWarnings("synthetic-access")
	public static class Sql {
		@SuppressWarnings("javadoc")
		public static final String 	SQL_HOST = "SQL_HOST",
																SQL_PORT = "SQL_PORT",
																SQL_USER = "SQL_USER",
																SQL_PASSWORD = "SQL_PASSWORD";
		@SuppressWarnings("javadoc")
		public static final String 	INIT_DATABASE_SCRIPT = "INIT_DATABASE_SCRIPT",
																CREATE_ROLES_SCRIPT = "CREATE_ROLES_SCRIPT";
		
		private static Properties props;
		
		private static void init() {
			props = new Properties(ConfigFiles.get(ConfigFiles.SQL_CONFIG), Defaults.sql());
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
		
		/** @return configured SQL server address */
		public static String getServer() {
			return get(SQL_HOST);
		}
		/** @return configured SQL server port */
		public static int getPort() {
			return Integer.parseInt(get(SQL_PORT));
		}
		/** @return configured SQL user */
		public static String getUser() {
			return get(SQL_USER);
		}
		/** @return configured SQL password */
		public static String getPassword() {
			return get(SQL_PASSWORD);
		}
	}
	
	/**
	 * Returns pre-built strings.
	 */
	public static class Strings {
		@SuppressWarnings("javadoc")
		public static final String CANNOT_FIND_LOGFILE = "Unable to locate log file for this class";
	}
}
