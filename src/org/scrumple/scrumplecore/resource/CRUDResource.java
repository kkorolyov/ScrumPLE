package org.scrumple.scrumplecore.resource;

import java.util.Map;
import java.util.UUID;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.scrumple.scrumplecore.database.DAO;

import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * Provides for creation, retrieval, update, and deletion of resources via HTML requests.
 * @param <T> type handled by this resource
 */
public abstract class CRUDResource<T> {
	private final DAO<T> dao;
	
	/**
	 * Constructs a new resource backed by a {@code DAO}.
	 * @param dao data access object for resource data
	 */
	public CRUDResource(DAO<T> dao) {
		this.dao = dao;
	}
	
	/**
	 * Creates a new resource.
	 * @param obj resource to create
	 * @return id of created resource
	 */
	@POST
	public UUID create(T obj) {
		return dao.add(obj);
	}
	
	/**
	 * Retrieves a resource.
	 * @param id id of resource to retrieve
	 * @return retrieved resource
	 */
	@GET
	@Path("{uuid}")
	public T retrieve(@PathParam("uuid") UUID id) {
		return dao.get(id);
	}
	/**
	 * Retrieves a collection of resources.
	 * @param uriInfo request URI info
	 * @return optionally-filtered collection of resources
	 */
	@GET
	public Map<UUID, T> retrieve(@Context UriInfo uriInfo) {
		return dao.get(buildRetrieveCondition(uriInfo.getQueryParameters()));
	}
	/**
	 * Builds and returns an optional retrieval filter/condition in response to query parameters.
	 * @param queryParams query parameters passed in request
	 * @return appropriate filter
	 */
	protected abstract Condition buildRetrieveCondition(MultivaluedMap<String, String> queryParams);
	
	/**
	 * Updates a resource.
	 * @param id id of resource to update
	 * @param replacement replacement resource
	 */
	@PUT
	@Path("{uuid}")
	public void update(@PathParam("uuid") UUID id, T replacement) {
		dao.update(id, replacement);
	}
	
	/**
	 * Deletes a resource.
	 * @param id id of resource to delete
	 * @return deleted resource
	 */
	@Path("{uuid}")
	public T delete(@PathParam("uuid") UUID id) {
		return dao.remove(id);
	}
}
