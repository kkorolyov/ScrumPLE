package org.scrumple.scrumplecore.database;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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
	
	@Test
	@SuppressWarnings("synthetic-access")
	public void testSave() throws SQLException {
		Assets.init();
		Database db = new Database(Sql.getUrl(), Sql.getUser(), Sql.getPassword());
		db.init();
		
		System.out.println(db.save(new StubSaveable()));
	}
	
	private class StubSaveable implements Saveable {
		@Override
		public List<Object> toData() {
			return Arrays.asList(new Object[]{null, 1, null, 'c'});
		}
	}
}
