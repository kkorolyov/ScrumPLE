package org.scrumple.scrumplecore.resource;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

class Entity<T> {	// TODO Providers
	@XmlAttribute
	final String uuid;
	@XmlElement
	final T data;
	
	@SuppressWarnings("unused")
	private Entity() {
		this(null, null);
	}
	Entity(UUID uuid, T data) {
		this.uuid = uuid.toString();
		this.data = data;
	}
}
