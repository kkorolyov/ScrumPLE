package org.scrumple.scrumplecore.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.scrumple.scrumplecore.auth.Authorizer;
import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.database.DAO;
import org.scrumple.scrumplecore.scrum.User;
import org.scrumple.scrumplecore.auth.UserSession;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;
import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * Provides for creation, retrieval, update, and deletion of resources via HTML requests.
 * @param <T> type handled by this resource
 */
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public abstract class CRUDResource<T> {
	private static final Logger log = Logger.getLogger(CRUDResource.class.getName(), Level.DEBUG);

	final DAO<T> dao;
	private final Map<String, Authorizer> authorizers = new HashMap<>();
	private DAO<UserSession> sessionDAO;

	/**
	 * Constructs a new CRUD resource.
	 * @param dao object providing access to resource data
	 */
	public CRUDResource(DAO<T> dao) {
		this.dao = dao;

		log.debug(() -> "Constructed new " + this);
	}

	/**
	 * Creates a new resource.
	 * @param obj resource to create
	 * @param headers request's HTTP headers
	 * @return id of created resource
	 */
	@POST
	public UUID create(T obj, @Context HttpHeaders headers) {
		getAuthorizer("POST").process(extractUser(headers));

		log.debug(() -> "Received POST with content=" + obj);
		return dao.add(obj);
	}

	/**
	 * Retrieves a resource.
	 * @param id id of resource to retrieve
	 * @param headers request's HTTP headers
	 * @return retrieved resource
	 */
	@GET
	@Path("{uuid}")
	public T retrieve(@PathParam("uuid") UUID id, @Context HttpHeaders headers) {
		getAuthorizer("GET").process(extractUser(headers));

		log.debug(() -> "Received GET for id=" + id);
		log.severe(() -> headers.toString());
		return dao.get(id);
	}
	/**
	 * Retrieves a collection of resources.
	 * @param uriInfo request URI info
	 * @param headers request's HTTP headers
	 * @return optionally-filtered collection of resources
	 */
	@GET
	public Map<UUID, T> retrieve(@Context UriInfo uriInfo, @Context HttpHeaders headers) {
		getAuthorizer("GET").process(extractUser(headers));

		Condition query = parseQuery(uriInfo.getQueryParameters());

		log.debug(() -> "Received GET with query=" + query);
		return dao.get(query);
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
	public void update(@PathParam("uuid") UUID id, T replacement, @Context HttpHeaders headers) {
		getAuthorizer("PUT").process(extractUser(headers));

		log.debug(() -> "Received PUT with id=" + id + " content=" + replacement);
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
	public T delete(@PathParam("uuid") UUID id, @Context HttpHeaders headers) {
		getAuthorizer("DELETE").process(extractUser(headers));

		log.debug(() -> "Received DELETE with id=" + id);
		return dao.remove(id);
	}

	private Authorizer getAuthorizer(String identifier) {
		Authorizer authorizer = authorizers.get(identifier);
		if (authorizer == null)	authorizer = Authorizers.ALL;

		return authorizer;
	}

	/**
	 * Sets a common authorizer for all methods.
	 * @param authorizer authorizer for all methods, does nothing if {@code null}
	 * @param authDAO DAO providing access to auth data
	 */
	public void setAuthorizers(Authorizer authorizer, DAO<UserSession> authDAO) {
		setAuthorizers(authorizer, authorizer, authorizer, authorizer, authDAO);
	}
	/**
	 * Sets the authorizers used by this resource. {@code null} values do nothing.
	 * @param POST authorizer for {@code POST} requests
	 * @param GET authorizer for {@code GET} requests
	 * @param PUT authorizer for {@code PUT} requests
	 * @param DELETE authorizer for {@code DELETE} requests
	 * @param authDAO DAO providing access to auth data
	 */
	public void setAuthorizers(Authorizer POST, Authorizer GET, Authorizer PUT, Authorizer DELETE, DAO<UserSession> authDAO) {
		setAuthorizer("POST", POST);
		setAuthorizer("GET", GET);
		setAuthorizer("PUT", PUT);
		setAuthorizer("DELETE", DELETE);

		this.sessionDAO = authDAO;
	}
	private void setAuthorizer(String identifier, Authorizer authorizer) {
		if (authorizer != null)
			authorizers.put(identifier, authorizer);
	}


	private User extractUser(HttpHeaders headers) {
		if (sessionDAO == null) return null;

		UserSession session = UserSession.fromHeaders(headers, sessionDAO);

		return (session == null) ? null : session.getUser();
	}

	@Override
	public String toString() {
		return getClass().getName() + "{"
					 + "dao=" + dao
					 + ", authorizers=" + authorizers
					 + ", sessionDAO=" + sessionDAO
					 + "}";
	}
}
