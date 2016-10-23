package org.scrumple.scrumplecore.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
@SuppressWarnings("javadoc")
public class SqlReaderTest {
	@Parameters(name = "File({0})")
	public static Object[] data() {
		return new File[]{new File("sql/create-project-instance.sql"),
											new File("sql/create-project-instance_uber-params.sql")};
	}
	private final File file;
	
	public SqlReaderTest(File input) {
		file = input;
	}
	
	@Test
	public void testRead() throws FileNotFoundException {
		System.out.println("TestRead - " + file.getName());
		for (String statement : SqlReader.read(file))
			System.out.println(statement);
		System.out.println();
	}
	@Test
	public void testReadParams() throws FileNotFoundException {
		System.out.println("TestReadParams - " + file.getName());
		for (String statement : SqlReader.read(file, getStubParameters()))
			System.out.println(statement);
		System.out.println();
	}
	
	private static final List<String> getStubParameters() {
		return Arrays.asList("Var0", "Var1", "Var2");
	}
}
