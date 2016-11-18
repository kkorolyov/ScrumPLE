package org.scrumple.scrumplecore.applications;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.scrumple.scrumplecore.database.Saveable;

@XmlRootElement
public class Role implements Saveable {
	@XmlAttribute
	private String value;
	
	public Role(){}
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
