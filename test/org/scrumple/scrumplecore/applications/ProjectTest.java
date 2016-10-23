package org.scrumple.scrumplecore.applications;

import java.sql.SQLException;

import org.junit.Test;

import org.scrumple.scrumplecore.assets.Assets;
import org.scrumple.scrumplecore.assets.Assets.Sql;


@SuppressWarnings("javadoc")
public class ProjectTest {
	@Test
	public void testDatabase() throws SQLException { // TODO Not a test
		Assets.init();
		Project p = new Project(Sql.get(Sql.SQL_HOST), Sql.get(Sql.SQL_PORT), Sql.get(Sql.SQL_USER), Sql.get(Sql.SQL_PASSWORD));
		p.createProject();
	}
}
