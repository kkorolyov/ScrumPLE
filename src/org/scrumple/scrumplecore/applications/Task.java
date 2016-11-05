package org.scrumple.scrumplecore.applications;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.scrumple.scrumplecore.database.Saveable;

public class Task implements Saveable{
	private String taskName;
	private int taskType;  //Is it a feature, bug, etc.  Do we need a Label class, or can we just include it as a String field in Task? Current save method will require that we have a Label class.
	private String taskDescription;
	private boolean done;
	
	public Task(String name, int type, String taskDescription) {
		this.taskName = name;
		this.taskType = type;
		this.taskDescription = taskDescription;
		this.done = false;
	}
	
	public String getTaskName() {
		return this.taskName;
	}
	
	public int getTaskType() {
		return this.taskType;
	}
	
	public String getTaskDescription() {
		return this.taskDescription;
	}
	
	public void updateDescription(String newDes) {
		taskDescription = newDes;
	}
	public boolean isFinished() {
		return done;
	}

	@Override
	public List<Object> toData() {
		// TODO Auto-generated method stub
		return Arrays.asList(new Object[]{taskName, taskType, taskDescription});
	}

	@Override
	public void fromData(List<Object> data) {
		Iterator <Object> it = data.iterator();
		this.taskName = (String) it.next();
		this.taskType = (int) it.next();
		this.taskDescription = (String) it.next();
	}
}
