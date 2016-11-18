package org.scrumple.scrumplecore.resource;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.scrumple.scrumplecore.applications.Project;
import org.scrumple.scrumplecore.applications.Task;
import org.scrumple.scrumplecore.applications.User;

@XmlRootElement
@XmlSeeAlso({Project.class, User.class, Task.class})
class Entity {
	@XmlAttribute
	final String uuid;
	@XmlElement
	final Object data;
	
	@SuppressWarnings("unused")
	private Entity() {
		this(null, null);
	}
	Entity(UUID uuid, Object data) {
		this.uuid = uuid.toString();
		this.data = data;
	}
}
