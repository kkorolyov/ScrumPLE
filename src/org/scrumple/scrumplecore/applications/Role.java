package org.scrumple.scrumplecore.applications;

import java.util.Arrays;
import java.util.List;

import org.scrumple.scrumplecore.database.Saveable;

public class Role implements Saveable {
	private String value;
	
	public Role(String value) {
		this.value = value;
	}

	@Override
	public List<Object> toData() {
		return Arrays.asList(value);
	}

	@Override
	public void fromData(List<Object> data) {
		value = (String) data.iterator().next();
	}
}
