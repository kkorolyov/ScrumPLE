package org.scrumple.scrumplecore.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Converts SQL statements from files into Strings.
 */
public class SqlReader {
	private static final String COMMENT = "--",
															SPACE = " ";
	
	/**
	 * Reads a file of SQL statements into an array of statements.
	 * @param sqlFile file to read
	 * @return array of all SQL statements in file
	 * @throws FileNotFoundException if {@code sqlFile} is {@code null} or not found
	 */
	public static final List<String> read(File sqlFile) throws FileNotFoundException {
		if (sqlFile == null)
			throw new FileNotFoundException();
		
		List<String> statements = new ArrayList<>();
		StringBuilder statementBuilder = new StringBuilder();
		
		try (Scanner in = new Scanner(sqlFile)) {
			while (in.hasNextLine()) {
				String line = in.nextLine();
				
				if (line.length() <= 0 || line.startsWith(COMMENT)) {	// Reached end of a statement
					if (statementBuilder.length() > 0) {
						statements.add(statementBuilder.toString());
						
						statementBuilder = new StringBuilder();	// Clear builder
					}
				} else	// In start or middle of statement
					statementBuilder.append(SPACE).append(line.trim());
			}
			if (statementBuilder.length() > 0)	// Check buffer at end of file
				statements.add(statementBuilder.toString());
		}
		return statements;
	}
}
