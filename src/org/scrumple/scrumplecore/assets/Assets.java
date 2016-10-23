package org.scrumple.scrumplecore.assets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

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
		PropFiles.init();
		LogFiles.init();
		Sql.init();
		
		try {
			log.addWriter(new PrintWriter(LogFiles.get(Assets.class)));
		} catch (Exception e) {
			log.severe("Unable to locate log file for this class");
		}
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
	private static class Defaults {
		public static final String PROPFILES_FILE = "config/configs.ini";
		public static final String 	PROPS_LOGGERS = "config/logging.ini",
																PROPS_SQL = "config/sql.ini";
		public static final String 	SYSTEM_SCHEMA = "System",
																PROJECT_SCHEMA = "Project";
		
		private static Properties propFiles() {
			log.debug("Building defaults for PropFiles...");

			return buildDefaultsForClass(PropFiles.class);
		}
		private static Properties logFiles() {
			log.debug("Building defaults for LogFiles...");
			
			Properties props = new Properties();
			props.put(Assets.class.getName(), "logs/Assets.log");
			
			return props;
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
	private static class PropFiles {
		public static final String 	PROPS_LOGGERS = "PROPS_LOGGERS",
																PROPS_SQL = "PROPS_SQL";
		
		private static Properties props;
		
		private static void init() {
			props = new Properties(new File(Defaults.PROPFILES_FILE), Defaults.propFiles());
			save(props);
			
			log.debug("Loaded PropFiles properties");
		}
		
		public static File get(String key) {
			String fileName = props.get(key);
			
			return fileName != null ? new File(fileName) : null;
		}
	}
	/**
	 * Returns corresponding log files for classes. 
	 */
	@SuppressWarnings("synthetic-access")
	public static class LogFiles {
		private static Properties props;
		
		private static void init() {
			props = new Properties(PropFiles.get(PropFiles.PROPS_LOGGERS), Defaults.logFiles());
			save(props);
			
			log.debug("Loaded LogFiles properties");
		}
		
		/**
		 * @return log file for {@code classClass}, or {@code null} if a log file for the class is not specified;
		 * if a file is specified but does not exist,	it is created.
		 */
		public static File get(Class<?> classClass) {
			String fileName = props.get(classClass.getName());
			if (fileName == null)
				return null;
			
			File file = new File(fileName);
			if (!file.isFile()) {
				File parent = file.getParentFile();
				if (parent != null && !parent.exists()) {
					parent.mkdirs();
				}
			}
			return file;
		}
	}
	/**
	 * Returns SQL properties and statements.
	 */
	@SuppressWarnings({"javadoc", "synthetic-access"})
	public static class Sql {
		public static final String 	SQL_HOST = "SQL_HOST",
																SQL_PORT = "SQL_PORT",
																SQL_USER = "SQL_USER",
																SQL_PASSWORD = "SQL_PASSWORD";
		public static final String 	SYSTEM_SCHEMA = "SYSTEM_SCHEMA",
																PROJECT_SCHEMA = "PROJECT_SCHEMA";
		public static final String RELEASES = "RELEASES";
		public static final String PROJECT = "PROJECT";
		
		private static Properties props;
		
		private static void init() {
			props = new Properties(PropFiles.get(PropFiles.PROPS_SQL), Defaults.sql());
			//We can either use props.put or manually write the ini file.  Which way is better?
			props.put(RELEASES, "CREATE TABLE IF NOT EXISTS RELEASES (id INT UNSIGNED NOT NULL AUTO_INCREMENT, description VARCHAR(64) NOT NULL, start DATE NOT NULL, end DATE NOT NULL, PRIMARY KEY(id))");
			props.put(PROJECT, "CREATE TABLE IF NOT EXISTS Project " + "(name VARCHAR(64), description VARCHAR(256), PRIMARY KEY (name))");
			save(props);
			
			log.debug("Loaded SQL properties");
		}
		
		/** @return SQL value mapped to {@code key}, or {@code null} if no such key */
		public static String get(String key) {
			return props.get(key);
		}
	}
}
