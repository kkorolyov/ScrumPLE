package org.scrumple.scrumplecore.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Converts SQL statements from files into Strings.
 */
public class SqlReader {
	private static final String COMMENT = "--",
															SPACE = " ";
	private static final String VARIABLE = "$",
															VARIABLE_REGEX = "\\" + VARIABLE + "\\w+";	// Matches a word prefixed by variable marker
	
	/**
	 * Reads a file of SQL statements into an array of statements.
	 * @see #read(File, List)
	 */
	public static final List<String> read(File sqlFile) throws FileNotFoundException {
		return read(sqlFile, null);
	}
	/**
	 * Reads a file of SQL statements into an array of statements.
	 * @param sqlFile file to read
	 * @param params parameters to substitute into {@code $} markers in read SQL statement in order of appearance
	 * @return array of all SQL statements in file
	 * @throws FileNotFoundException if {@code sqlFile} is {@code null} or not found
	 */
	public static final List<String> read(File sqlFile, List<String> params) throws FileNotFoundException {
		if (sqlFile == null)
			throw new FileNotFoundException();
		
		List<String> statements = new ArrayList<>();
		StringBuilder statementBuilder = new StringBuilder();
		Iterator<String> paramsIterator = params != null ? params.iterator() : null;
		
		try (Scanner in = new Scanner(sqlFile)) {
			while (in.hasNextLine()) {
				String line = in.nextLine().trim();
				
				if (line.length() <= 0 || line.startsWith(COMMENT)) {	// Reached end of a statement
					if (statementBuilder.length() > 0) {
						statements.add(statementBuilder.toString());
						
						statementBuilder = new StringBuilder();	// Clear builder
					}
				} else {	// In start or middle of statement
					while (line.contains(VARIABLE) && paramsIterator != null && paramsIterator.hasNext())
						line = line.replaceFirst(VARIABLE_REGEX, paramsIterator.next());
					
					statementBuilder.append(SPACE).append(line);
				}
			}
			if (statementBuilder.length() > 0)	// Check buffer at end of file
				statements.add(statementBuilder.toString());
		}
		return statements;
	}
}
