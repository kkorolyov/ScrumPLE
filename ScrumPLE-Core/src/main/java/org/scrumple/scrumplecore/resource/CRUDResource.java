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
	private final Map<String, Authorizer> authorizers = new HashMap<>();
	
	/**
	 * Constructs a new CRUD resource.
	 * @param dao object providing access to resource data
	 */
	public CRUDResource(DAO<T> dao) {
		this.dao = dao;
	}

	/**
	 * Creates a new resource.
	 * @param obj resource to create
	 * @param headers request's HTTP headers
	 * @return id of created resource
	 */
	@POST
	public UUID create(T obj, @Context HttpHeaders headers) throws AuthorizationException {
		getAuthorizer("POST").process(extractCredentials(headers));

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
		getAuthorizer("GET").process(extractCredentials(headers));

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
		getAuthorizer("GET").process(extractCredentials(headers));

		return dao.get(parseQuery(uriInfo.getQueryParameters()));
	}
	/**
	 * Constructs an optional retrieval filter/condition from query parameters.
	 * @param queryParams query parameters passed in request
	 * @return appropriate filter
	 */
	protected abstract Condition parseQuery(MultivaluedMap<String, String> queryParams);
	
	/**
	 * Updates a resource.
	 * @param id id of resource to update
	 * @param replacement replacement resource
	 * @param headers request's HTTP headers
	 */
	@PUT
	@Path("{uuid}")
	public void update(@PathParam("uuid") UUID id, T replacement, @Context HttpHeaders headers) throws AuthorizationException {
		getAuthorizer("PUT").process(extractCredentials(headers));

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
		getAuthorizer("DELETE").process(extractCredentials(headers));

		return dao.remove(id);
	}

	/** @return data accessor used by this resource */
	public DAO<T> getDAO() {
		return dao;
	}

	/**
	 * Sets a common authorizer for all methods.
	 * @param authorizer authorizer for all methods, does nothing if {@code null}
	 */
	public void setAuthorizers(Authorizer authorizer) {
		setAuthorizers(authorizer, authorizer, authorizer, authorizer);
	}
	/**
	 * Sets the authorizers used by this resource. {@code null} values do nothing.
	 * @param POST authorizer for {@code POST} requests
	 * @param GET authorizer for {@code GET} requests
	 * @param PUT authorizer for {@code PUT} requests
	 * @param DELETE authorizer for {@code DELETE} requests
	 */
	public void setAuthorizers(Authorizer POST, Authorizer GET, Authorizer PUT, Authorizer DELETE) {
		setAuthorizer("POST", POST);
		setAuthorizer("GET", GET);
		setAuthorizer("PUT", PUT);
		setAuthorizer("DELETE", DELETE);
	}
	private void setAuthorizer(String identifier, Authorizer authorizer) {
		if (authorizer != null)
			authorizers.put(identifier, authorizer);
	}

	private Credentials extractCredentials(HttpHeaders headers) {
		List<String> authHeaders = headers == null ? null : headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
		return (authHeaders == null || authHeaders.isEmpty()) ? null : new Credentials(authHeaders.iterator().next().replaceFirst("^.*?\\s+", ""));	// Remove all before space
	}

	private Authorizer getAuthorizer(String identifier) {
		Authorizer authorizer = authorizers.get(identifier);
		if (authorizer == null)
			authorizer = Authorizers.ALL;
		return authorizer;
	}
}
