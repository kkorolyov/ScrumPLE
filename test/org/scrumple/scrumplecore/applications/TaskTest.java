package org.scrumple.scrumplecore.applications;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;

import org.junit.Test;

import org.scrumple.scrumplecore.assets.Assets;
import org.scrumple.scrumplecore.database.Database;


@SuppressWarnings("javadoc")
public class TaskTest {
	@Test
	public void testDatabase() throws SQLException { // TODO Not a test
		Assets.init();
		Task t1 = new Task(1, "Make ScrumPLE great again");
		t1.updateDescription("Let's do it");
		Task t2 = new Task(2, "Build the wall");
		t2.updateDescription("The wall is big");
		try {
			Database db = new Database("Project", null);
			//db.save(t1);
			//db.save(t2);
			Task t3 = db.load(4);
			Task t4 = db.load(5);
			Set<Task> tasks = new HashSet<Task>();
			tasks = db.loadSimilarTasks(1);
			
			System.out.println(tasks.size());
			for(Task task : tasks) {
				System.out.println(task.getTaskDescription());
			}
			//System.out.println(t3.getTaskDescription());
			//System.out.println(t4.getTaskDescription());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
