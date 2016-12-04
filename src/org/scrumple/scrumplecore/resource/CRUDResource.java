package org.scrumple.scrumplecore.resource;

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;

/**
 * A resource providing all CRUD methods.
 * @param <T> type of resource on which methods executed
 */
public interface CRUDResource<T> {
	/**
	 * Creates a new resource.
	 * @param toCreate resource to create
	 * @return id of created resource
	 * @throws SQLException if a database error occurs
	 */
	UUID create(T toCreate) throws SQLException;
	
	/**
	 * Retrieves a resource.
	 * @param toRetrieve id of resource to retrieve
	 * @return retrieved resource wrapped in an {@code Entity}
	 * @throws SQLException if a database error occurs
	 */
	T retrieve(UUID toRetrieve) throws SQLException;
	/**
	 * Retrieves all resources.
	 * @param uriInfo request URI info
	 * @return all resources
	 * @throws SQLException if a database error occurs
	 */
	Set<Entity> retrieveAll(UriInfo uriInfo) throws SQLException;
	
	/**
	 * Updates a resource.
	 * @param toUpdate id of resource to update
	 * @param replacement replacement resource
	 * @return {@code true} if update successful
	 * @throws SQLException if a database error occurs
	 */
	boolean update(UUID toUpdate, T replacement) throws SQLException;
	
	/**
	 * Deletes a resource.
	 * @param toDelete id of resource to delete
	 * @return deleted resource wrapped in an {@code Entity}
	 * @throws SQLException if a database error occurs
	 */
	T delete(UUID toDelete) throws SQLException;
}
