package org.scrumple.scrumplecore.assets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;
import dev.kkorolyov.simpleprops.Properties;

/**
 * Global access to all external assets.
 */
public final class Assets {
	@SuppressWarnings("javadoc")
	public static final String 	DB_HOST = "databaseHost",
															DB_PORT = "databasePort",
															DB_USER = "databaseUser",
															DB_PASSWORD = "databasePassword";
	@SuppressWarnings("javadoc")
	public static final String 	SYSTEM_DB = "systemDatabase";

	private static final String LOG_PROPS_FILE = "config/logging.ini";
	
	private static final Logger log = Logger.getLogger(Assets.class.getName(), Level.DEBUG);
	private static Properties config = new Properties();
	
	private static Path prepareFile(Path file) throws IOException {
		if (!Files.exists(file)) {
			Path parent = file.getParent();
			if (parent != null)
				Files.createDirectories(parent);
			
			Files.createFile(file);
		}
		return file;
	}
	
	private static Properties generateDefaults() {
		Properties defaults = new Properties();
		
		defaults.putComment("Database connection");
		defaults.put(DB_HOST, "<URL or IP address>");
		defaults.put(DB_PORT, "");
		defaults.put(DB_USER, "");
		defaults.put(DB_PASSWORD, "");
		defaults.putBlankLine();
		
		defaults.putComment("Database constants");
		defaults.put(SYSTEM_DB, "ScrumPLE");
		
		return defaults;
	}
	
	/**
	 * @param key property identifier
	 * @return value of property defined by {@code key}
	 */
	public static String get(String key) {
		return config.get(key);
	}
	
	/**
	 * @param key property identifier
	 * @return value of property defined by {@code key} parsed into a positive {@code int}, or {@code -1} if value is not a positive {@code int}
	 */
	public static int getInt(String key) {
		String value = get(key);
		return isInt(value, 10) ? Integer.parseInt(value) : -1;
	}
	private static boolean isInt(String s, int radix) {
		for (char c : s.toCharArray()) {
			if (Character.digit(c, radix) < 0)
				return false;
		}
		return true;
	}

	/**
	 * Applies properties to the global configuration, overwriting preexisiting properties of the same name.
	 * @param properties all properties to apply
	 */
	public static void applyConfig(Properties properties) {
		config.put(properties, true);
	}
}
