package org.scrumple.scrumplecore.database;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scrumple.scrumplecore.applications.User;
import org.scrumple.scrumplecore.assets.Assets;

@SuppressWarnings("javadoc")
public class DatabaseTest {
	private static final String PROJECT_DB = "Project";
	
	private Database db;
	
	@BeforeClass
	public static void setUpBeforeClass() throws NamingException {
		Assets.init();
	}
	
	@Before
	public void setUp() throws NamingException, SQLException {
		db = new Database(PROJECT_DB);
	}
	
	@Test
	public void testCreateDatabase() {
		Database.createDatabase("Test");
	}
	
	@Test
	public void testSave() throws SQLException {
		//System.out.println(db.save(new StubSaveable()));
		System.out.println(db.save(new User("Cred", "Name", 9)));
	}
	
	private class StubSaveable implements Saveable {
		@Override
		public List<Object> toData() {
			return Arrays.asList(new Object[]{null, 1, null, 'c'});
		}
	}
}
