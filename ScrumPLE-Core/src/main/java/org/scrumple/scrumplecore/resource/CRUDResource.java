package org.scrumple.scrumplecore.resource;

import dev.kkorolyov.sqlob.persistence.Condition;
import org.scrumple.scrumplecore.auth.AuthorizationException;
import org.scrumple.scrumplecore.auth.Authorizer;
import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.auth.Credentials;
import org.scrumple.scrumplecore.database.DAO;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Provides for creation, retrieval, update, and deletion of resources via HTML requests.
 * @param <T> type handled by this resource
 */
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public abstract class CRUDResource<T> {
	private final DAO<T> dao;
	private final Authorizer authorizer;
	
	/**
	 * Constructs a new CRUD resource with the {@link org.scrumple.scrumplecore.auth.Authorizers#ALWAYS} authorizer.
	 * @param dao data access object for resource data
	 */
	public CRUDResource(DAO<T> dao) {
		this(dao, Authorizers.ALWAYS);
	}

	/**
	 * Constructs a new CRUD resource.
	 * @param dao object providing access to resource data
	 * @param authorizer method authorizer
	 */
	public CRUDResource(DAO<T> dao, Authorizer authorizer) {
		this.dao = dao;
		this.authorizer = authorizer;
	}
	
	/**
	 * Creates a new resource.
	 * @param obj resource to create
	 * @param headers request's HTTP headers
	 * @return id of created resource
	 */
	@POST
	public UUID create(T obj, @Context HttpHeaders headers) throws AuthorizationException {
		Credentials credentials = extractCredentials(headers);
		if (!authorizer.canPOST(credentials)) {
			throw new AuthorizationException(credentials, "POST");
		}
		return dao.add(obj);
	}
	/**
	 * Creates a new resource from form parameters.
	 * @param params supplied form parameters
	 * @param headers request's HTTP headers
	 * @return id of created resource
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public UUID create(MultivaluedMap<String, String> params, @Context HttpHeaders headers) throws AuthorizationException {
		return create(parseForm(params), headers);
	}
	/**
	 * Constructs an instance of {@code T} from form parameters.
	 * @param params supplied form parameters
	 * @return instance representing data supplied in {@code params}
	 */
	protected abstract T parseForm(MultivaluedMap<String, String> params);
	
	/**
	 * Retrieves a resource.
	 * @param id id of resource to retrieve
	 * @param headers request's HTTP headers
	 * @return retrieved resource
	 */
	@GET
	@Path("{uuid}")
	public T retrieve(@PathParam("uuid") UUID id, @Context HttpHeaders headers) throws AuthorizationException {
		Credentials credentials = extractCredentials(headers);
		if (!authorizer.canGET(credentials)) {
			throw new AuthorizationException(credentials, "GET");
		}
		return dao.get(id);
	}
	/**
	 * Retrieves a collection of resources.
	 * @param uriInfo request URI info
	 * @param headers request's HTTP headers
	 * @return optionally-filtered collection of resources
	 */
	@GET
	public Map<UUID, T> retrieve(@Context UriInfo uriInfo, @Context HttpHeaders headers) throws AuthorizationException {
		Credentials credentials = extractCredentials(headers);
		if (!authorizer.canGET(credentials)) {
			throw new AuthorizationException(credentials, "GET");
		}
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
	 * @param headers request's HTTP headers
	 */
	@PUT
	@Path("{uuid}")
	public void update(@PathParam("uuid") UUID id, T replacement, @Context HttpHeaders headers) throws AuthorizationException {
		Credentials credentials = extractCredentials(headers);
		if (!authorizer.canPUT(credentials)) {
			throw new AuthorizationException(credentials, "PUT");
		}
		dao.update(id, replacement);
	}
	
	/**
	 * Deletes a resource.
	 * @param id id of resource to delete
	 * @param headers request's HTTP headers
	 * @return deleted resource
	 */
	@DELETE
	@Path("{uuid}")
	public T delete(@PathParam("uuid") UUID id, @Context HttpHeaders headers) throws AuthorizationException {
		Credentials credentials = extractCredentials(headers);
		if (!authorizer.canDELETE(credentials)) {
			throw new AuthorizationException(credentials, "DELETE");
		}
		return dao.remove(id);
	}

	private Credentials extractCredentials(HttpHeaders headers) {
		List<String> authHeaders = headers == null ? null : headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
		return (authHeaders == null || authHeaders.isEmpty()) ? null : new Credentials(authHeaders.iterator().next().replaceFirst("^.*?\\s+", ""));	// Remove all before space
	}
}
