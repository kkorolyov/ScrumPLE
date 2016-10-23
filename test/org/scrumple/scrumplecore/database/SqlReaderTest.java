package org.scrumple.scrumplecore.database;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
@SuppressWarnings("javadoc")
public class SqlReaderTest {
	@Parameters(name = "File({0})")
	public static Object[] data() {
		return new File[]{new File("sql/CreateBlahTable.sql"),
											new File("sql/PopulateTestTable.sql")};
	}
	private final File file;
	
	public SqlReaderTest(File input) {
		file = input;
	}
	
	@Test
	public void testRead() throws FileNotFoundException {
		for (String statement : SqlReader.read(file))
			System.out.println(statement);
	}
}
