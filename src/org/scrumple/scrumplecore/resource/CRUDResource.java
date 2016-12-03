package org.scrumple.scrumplecore.resource;

import java.util.UUID;

/**
 * A resource providing all CRUD methods.
 * @param <T> type of resource on which methods executed
 */
public interface CRUDResource<T> {
	/**
	 * Creates a new resource.
	 * @param toCreate resource to create
	 * @return id of created resource
	 */
	UUID create(T toCreate);
	
	/**
	 * Retrieves a resource.
	 * @param toRetrieve id of resource to retrieve
	 * @return retrieved resource wrapped in an {@code Entity}
	 */
	Entity<T> retrieve(UUID toRetrieve);
	
	/**
	 * Updates a resource.
	 * @param toUpdate id of resource to update
	 * @param replacement replacement resource
	 * @return {@code true} if update successful
	 */
	boolean update(UUID toUpdate, T replacement);
	
	/**
	 * Deletes a resource.
	 * @param toDelete id of resource to delete
	 * @return deleted resource wrapped in an {@code Entity}
	 */
	Entity<T> delete(UUID toDelete);
}
