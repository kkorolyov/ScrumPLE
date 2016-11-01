package org.scrumple.scrumplecore.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scrumple.scrumplecore.assets.Assets;

@SuppressWarnings("javadoc")
public class DatabaseTest {
	private static final String PROJECT_DB = "Project";
	private static final String CREATE_STUB_TABLE = "CREATE TABLE IF NOT EXISTS StubSaveable (id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, v VARCHAR(64), i INT, r REAL, c CHAR(1), PRIMARY KEY (id))",
															DROP_STUB_TABLE = "DROP TABLE IF EXISTS StubSaveable";
	
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
		StubSaveable s = new StubSaveable("STRING", 54, 45.6, 't');

		db.executeBatch(DROP_STUB_TABLE);
		
		//assertEquals(-1, db.save(s));
		db.executeBatch(CREATE_STUB_TABLE);
		
		long saved = db.save(s);
		assertNotEquals(-1, saved);
		
		assertEquals(s, db.load(s.getClass(), saved));
		
		System.out.println(saved);
	}
	
	public static class StubSaveable implements Saveable {
		private String v;
		private int i;
		private double r;
		private char c;
		
		public StubSaveable() {
			// No-arg
		}
		public StubSaveable(String v, int i, double r, char c) {
			this.v = v;
			this.i = i;
			this.r = r;
			this.c = c;
		}
		
		@Override
		public List<Object> toData() {
			return Arrays.asList(new Object[]{"test", 1, .5, 'c'});
		}

		@Override
		public void fromData(List<Object> data) {
			Iterator<Object> it = data.iterator();
			
			v = (String) it.next();
			i = (int) it.next();
			r = (double) it.next();
			c = ((String) it.next()).charAt(0);
		}
	}
}
