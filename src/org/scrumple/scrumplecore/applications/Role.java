package org.scrumple.scrumplecore.applications;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Role {
	@XmlAttribute
	private String value;
	
	public Role(){}
	public Role(String value) {
		this.value = value;
	}
}
