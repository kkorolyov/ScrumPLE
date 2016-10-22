package org.scrumple.scrumplecore.assets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

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
			File assetsLog = LogFiles.get(Assets.class);
			if (!assetsLog.isFile()) {
				File parent = assetsLog.getParentFile();
				if (parent != null && !parent.exists()) {
					parent.mkdirs();
				}
			}
			log.addWriter(new PrintWriter(assetsLog));
		} catch (Exception e) {
			log.exception(e);
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
		
		private static Properties propFiles() {
			log.debug("Building defaults for PropFiles...");

			return buildDefaults(PropFiles.PROPS_LOGGERS,
													PropFiles.PROPS_SQL);
		}
		private static Properties logFiles() {
			log.debug("Building defaults for LogFiles...");
			
			Properties props = new Properties();
			props.put(Assets.class.getName(), "logs/Assets.log");
			
			return props;
		}
		private static Properties sql() {
			log.debug("Building defaults for sql...");

			return buildDefaults();	// No SQL defaults
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
		private static final String PROPS_LOGGERS = "PROPS_LOGGERS",
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
		
		/** @return log file for {@code classClass}, or {@code null} if a log file for the class is not specified */
		public static File get(Class<?> classClass) {
			String fileName = props.get(classClass.getName());
			
			return fileName != null ? new File(fileName) : null;
		}
	}
	/**
	 * Returns preset SQL statements.
	 */
	@SuppressWarnings("synthetic-access")
	public static class Sql {
		private static Properties props;
		
		private static void init() {
			props = new Properties(PropFiles.get(PropFiles.PROPS_SQL), Defaults.sql());
			save(props);
			
			log.debug("Loaded SQL properties");
		}
		
		/** @return SQL statement mapped to {@code key}, or {@code null} if no such statement */
		public static String get(String key) {
			return props.get(key);
		}
	}
}