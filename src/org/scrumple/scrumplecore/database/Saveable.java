package org.scrumple.scrumplecore.database;

/**
 * An entity which should be persisted.
 */
public interface Saveable {
	/** @return	saveable data */
	Object[] toData();
}
