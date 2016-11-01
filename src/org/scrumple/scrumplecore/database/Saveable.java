package org.scrumple.scrumplecore.database;

import java.util.List;

/**
 * An entity which should be persisted.
 */
public interface Saveable {
	/** @return	saveable data */
	List<Object> toData();
	
	/**
	 * Loads this object from data
	 * @param data data to load
	 */
	void fromData(List<Object> data);
}
