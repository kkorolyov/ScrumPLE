package org.scrumple.scrumplecore.applications;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.scrumple.scrumplecore.database.Saveable;

import dev.kkorolyov.sqlob.annotation.Transient;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Task implements Saveable{
	
	private int taskType;  //Is it a feature, bug, etc.  Do we need a Label class, or can we just include it as a String field in Task? Current save method will require that we have a Label class.
	@XmlElement
	private String taskDescription;
	@Transient
	private boolean done;
	
	public Task() {
		this.taskDescription = null;
		this.taskType = 0;
		this.done = false;
	}
	public Task(int type, String taskDescription) {
		this.taskDescription = taskDescription;
		this.taskType = type;
		this.done = false;
	}
	
	public int getTaskType() {
		return this.taskType;
	}
	
	public void setTaskType(int newType) {
		this.taskType = newType;
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
		return Arrays.asList(new Object[]{taskType, taskDescription});
	}

	@Override
	public void fromData(List<Object> data) {
		Iterator <Object> it = data.iterator();
		this.taskType = (int) it.next();
		this.taskDescription = (String) it.next();
	}
}
