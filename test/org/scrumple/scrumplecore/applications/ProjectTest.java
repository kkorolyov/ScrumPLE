package org.scrumple.scrumplecore.applications;

import java.sql.SQLException;

import org.junit.Test;

import org.scrumple.scrumplecore.assets.Assets;
import org.scrumple.scrumplecore.assets.Assets.Sql;

import org.scrumple.scrumplecore.database.*;


@SuppressWarnings("javadoc")
public class ProjectTest {
	@Test
	public void testProject() throws SQLException { // TODO Not a test
		Assets.init();
		User u1 = new User("12345", "Joe", 1);
		User u2 = new User("67890", "Bob", 2);
		Project p = new Project("Test Project", "This is a test");
		
		p.addUser(u1);
		p.addUser(u2);
		Database db = new Database(Sql.getUrl(), Sql.getUser(), Sql.getPassword());
		db.save(p);
		//db.save(p.getUsers());
	}
}
