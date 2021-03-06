package org.scrumple.scrumplecore.database;

import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import dev.kkorolyov.sqlob.utility.Condition;

/**
 * Provides access to object stored as persistent data.
 */
public interface DAO<T> {
	/** @return {@code true} if this DAO persists {@code obj} */
	boolean contains(T obj);

	/**
	 * Retrieves a uniquely-identified persisted object.
	 * @param id object identifier
	 * @return appropriate object
	 * @throws EntityNotFoundException if {@code id} does not match a persisted object
	 * @throws DataAccessException if an issue occurs during data access
	 */
	T get(UUID id);
	/** 
	 * @param cond filter restricting returned objects, {@code null} implies no filter
	 * @return all persisted objects matching {@code cond} as <code>{id, object}</code> pairs
	 * @throws DataAccessException if an issue occurs during data access
	 */
	Map<UUID, T> get(Condition cond);
	/**
	 * @return all persisted objects of type {@code T} as <code>{id, object}</code> pairs
	 * @throws DataAccessException if an issue occurs during data access
	 * @see #get(Condition)
	 */
	Map<UUID, T> getAll();
	
	/**
	 * Updates a persisted object with a new instance.
	 * @param id object identifier
	 * @param newObj updated instance
	 * @throws EntityNotFoundException if {@code id} does not match a persisted object
	 * @throws DataAccessException if an issue occurs during data access
	 */
	void update(UUID id, T newObj);
	
	/**
	 * Persists an object.
	 * @param obj instance to persist
	 * @return persisted object identifier
	 * @throws DataAccessException if an issue occurs during data access
	 */
	UUID add(T obj);
	/**
	 * Removes a persisted object.
	 * @param id object identifier
	 * @return removed object
	 * @throws EntityNotFoundException if {@code id} does not match a persisted object
	 * @throws DataAccessException if an issue occurs during data access
	 */
	T remove(UUID id);
}
