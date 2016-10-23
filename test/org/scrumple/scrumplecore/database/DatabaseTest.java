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
		
		Database db = new Database(Sql.get(Sql.SQL_HOST), Sql.get(Sql.SQL_PORT), Sql.get(Sql.SQL_USER), Sql.get(Sql.SQL_PASSWORD));
		
		db.createDB(Sql.get(Sql.SYSTEM_SCHEMA));
		db.createDB(Sql.get(Sql.PROJECT_SCHEMA));
		
		db.createSystemSchema();
		db.createProjectSchema();
	}
}
