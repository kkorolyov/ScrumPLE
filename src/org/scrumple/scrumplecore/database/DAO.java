package org.scrumple.scrumplecore.database;

import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * Provides access to object stored as persistent data.
 */
public interface DAO<T> {
	/**
	 * Retrieves a uniquely-identified persisted object.
	 * @param id object identifier
	 * @return appropriate object
	 * @throws EntityNotFoundException if {@code id} does not match a persisted object
	 * @throws DataAccessException if an issue occurs during data access
	 */
	public T get(UUID id) throws EntityNotFoundException, DataAccessException;
	/** 
	 * @param cond filter restricting returned objects, {@code null} implies no filter
	 * @return all persisted objects matching {@code cond} as <code>{id, object}</code> pairs
	 * @throws DataAccessException if an issue occurs during data access
	 */
	public Map<UUID, T> get(Condition cond) throws DataAccessException;
	/**
	 * @return all persisted objects of type {@code T} as <code>{id, object}</code> pairs
	 * @throws DataAccessException if an issue occurs during data access
	 * @see #get(Condition)
	 */
	public Map<UUID, T> getAll() throws DataAccessException;
	
	/**
	 * Updates a persisted object with a new instance.
	 * @param id object identifier
	 * @param newObj updated instance
	 * @throws EntityNotFoundException if {@code id} does not match a persisted object
	 * @throws DataAccessException if an issue occurs during data access
	 */
	public void update(UUID id, T newObj) throws EntityNotFoundException, DataAccessException;
	
	/**
	 * Persists an object.
	 * @param obj instance to persist
	 * @return persisted object identifier
	 * @throws DataAccessException if an issue occurs during data access
	 */
	public UUID add(T obj) throws DataAccessException;
	/**
	 * Removes a persisted object.
	 * @param id object identifier
	 * @return removed object
	 * @throws EntityNotFoundException if {@code id} does not match a persisted object
	 * @throws DataAccessException if an issue occurs during data access
	 */
	public T remove(UUID id) throws EntityNotFoundException, DataAccessException;
}
