package org.scrumple.scrumplecore.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scrumple.scrumplecore.applications.Role;
import org.scrumple.scrumplecore.applications.User;
import org.scrumple.scrumplecore.assets.Assets;
import org.scrumple.scrumplecore.assets.Assets.Config;
import org.scrumple.scrumplecore.service.ServiceLoader;

import dev.kkorolyov.simpleprops.Properties;

@SuppressWarnings("javadoc")
public class DatabaseTest {
	private static final String PROJECT_DB = "Project";
	private static final String PARAM = "?",
															DROP_TABLE = "DROP TABLE IF EXISTS " + PARAM;
	private static final String SIMPLE_STUB_TABLE = "stub1",
															COMPLEX_STUB_TABLE = "stub2";
	private static final String CREATE_STUB_TABLE = "CREATE TABLE IF NOT EXISTS " + SIMPLE_STUB_TABLE + " (id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, v VARCHAR(64), i INT, r REAL, c CHAR(1), PRIMARY KEY (id))",
															CREATE_COMPLEX_STUB_TABLE = "CREATE TABLE IF NOT EXISTS " + COMPLEX_STUB_TABLE + " (id INT UNSIGNED AUTO_INCREMENT, i INT, stub BIGINT UNSIGNED, PRIMARY KEY (id), FOREIGN KEY (stub) REFERENCES " + SIMPLE_STUB_TABLE + " (id))";
	
	private Database db;
	
	@BeforeClass
	public static void setUpBeforeClass() throws NamingException {
		Assets.init();
	}
	
	@Before
	public void setUp() throws NamingException, SQLException {
		db = new Database(PROJECT_DB, buildStubSaveablesProperties());
	}
	
	@Test
	public void testCreateDropProject() throws SQLException, NamingException {
		String project = "TestProject";
		
		Database.dropProject(project);	// Cleanup

		Database.createProject(project);
		try {
			new Database(project, null);
		} catch (SQLException e) {
			fail("Project was not created");
		}
		Database.dropProject(project);
		try {
			new Database(project, null);
		} catch (SQLException e) {
			return;	// Success
		}
		fail("Project was not dropped");
	}
	
	@Test
	public void testSave() throws SQLException, InstantiationException, IllegalAccessException {
		Saveable 	simpleS = new StubSaveable("STRING", 54, 45.6, 't'),
							complexS = new ComplexStubSaveable(15, (StubSaveable) simpleS);

		db.executeBatch(drop(COMPLEX_STUB_TABLE),
										drop(SIMPLE_STUB_TABLE));
		db.executeBatch(CREATE_STUB_TABLE,
										CREATE_COMPLEX_STUB_TABLE);
		
		long simpleId = db.save(simpleS);
		assertNotEquals(-1, simpleId);
		assertEquals(simpleS, db.load(simpleS.getClass(), simpleId));
		
		long complexId = db.save(complexS);
		assertNotEquals(-1, complexId);
		assertEquals(complexS, db.load(complexS.getClass(), complexId));
	}
	
	@Test
	public void testREST() throws UnsupportedEncodingException, NoSuchAlgorithmException, SQLException, NamingException {	// TODO Not a test
		Role role = new Role("This is role");
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hashedPass = md.digest("password".getBytes("UTF-8"));
		
		User user = new User("UserBob", new String(hashedPass, "UTF-8"), role);
		
		Database db = new Database("", null);
		db.executeBatch("DROP SCHEMA IF EXISTS StubProject",
										"CREATE SCHEMA IF NOT EXISTS StubProject");
		
		db = new Database("StubProject", new Properties(Config.getFile(Config.SAVEABLES)));
		db.executeBatch("CREATE TABLE IF NOT EXISTS `role` (id INT UNSIGNED NOT NULL AUTO_INCREMENT, name VARCHAR(64) NOT NULL UNIQUE, PRIMARY KEY (id))",
										"CREATE TABLE IF NOT EXISTS `user` (id INT UNSIGNED NOT NULL AUTO_INCREMENT, handle VARCHAR(64) NOT NULL, password CHAR(64) NOT NULL, role INT UNSIGNED NOT NULL, PRIMARY KEY (id), INDEX role_idx (role ASC), CONSTRAINT `role` FOREIGN KEY (`role`)	REFERENCES `role` (id) ON DELETE NO ACTION ON UPDATE NO ACTION)");
		
		db.save(user);
	}
	
	private static String drop(String table) {
		return DROP_TABLE.replaceFirst(Pattern.quote(PARAM), table);
	}
	
	private static Properties buildStubSaveablesProperties() {
		Properties props = new Properties();
		
		props.put(SIMPLE_STUB_TABLE, StubSaveable.class.getName());
		props.put(ComplexStubSaveable.class.getName(), COMPLEX_STUB_TABLE);
		
		return props;
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
			return Arrays.asList(new Object[]{v, i, r, c});
		}

		@Override
		public void fromData(List<Object> data) {
			Iterator<Object> it = data.iterator();
			
			v = (String) it.next();
			i = (int) it.next();
			r = (double) it.next();
			c = ((String) it.next()).charAt(0);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			
			if (obj == null)
				return false;
			
			if (!(obj instanceof StubSaveable))
				return false;
			
			StubSaveable o = (StubSaveable) obj;
			if (v == null) {
				if (o.v != null)
					return false;
			} else if (!v.equals(o.v))
				return false;
			if (i != o.i)
				return false;
			if (r != o.r)
				return false;
			if (c != o.c)
				return false;
			
			return true;
		}
	}
	public static class ComplexStubSaveable implements Saveable {
		private int eent;
		private StubSaveable stubby;
		
		public ComplexStubSaveable() {
			// No-arg
		}
		public ComplexStubSaveable(int i, StubSaveable stub) {
			this.eent = i;
			this.stubby = stub;
		}

		@Override
		public List<Object> toData() {
			return Arrays.asList(new Object[]{eent, stubby});
		}
		@Override
		public void fromData(List<Object> data) {
			Iterator<Object> it = data.iterator();
			
			eent = (int) it.next();
			stubby = (StubSaveable) it.next();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			
			if (obj == null)
				return false;
			
			if (!(obj instanceof ComplexStubSaveable))
				return false;
			
			ComplexStubSaveable o = (ComplexStubSaveable) obj;
			if (eent != o.eent)
				return false;
			if (stubby == null) {
				if (o.stubby != null)
					return false;
			} else if (!stubby.equals(o.stubby))
				return false;
			
			return true;
		}
	}
}
