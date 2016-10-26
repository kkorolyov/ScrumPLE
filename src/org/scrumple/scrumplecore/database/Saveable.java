package org.scrumple.scrumplecore.database;

import java.util.List;

/**
 * An entity which should be persisted.
 */
public interface Saveable {
	/** @return	saveable data */
	List<Object> toData();
}
