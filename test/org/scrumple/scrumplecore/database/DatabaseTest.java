package org.scrumple.scrumplecore.database;

import java.sql.SQLException;

import org.junit.Test;
import org.scrumple.scrumplecore.assets.Assets;
import org.scrumple.scrumplecore.assets.Assets.Sql;

@SuppressWarnings("javadoc")
public class DatabaseTest {
	@Test
	public void testDatabase() throws SQLException { // TODO Not a test
		Assets.init();
		
		System.out.println(new Database(Sql.getUrl(), Sql.getUser(), Sql.getPassword()).init());
	}
}
