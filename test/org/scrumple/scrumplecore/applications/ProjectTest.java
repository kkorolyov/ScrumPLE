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
		Project p = new Project("Test Project", "This is a test");
		Database db = new Database(Sql.getUrl(), Sql.getUser(), Sql.getPassword());
		
		db.addProject(p);
	}
}
