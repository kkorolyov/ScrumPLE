package org.scrumple.scrumplecore.resource;

import javax.ws.rs.core.MultivaluedMap;

import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.Sprint;
import org.scrumple.scrumplecore.auth.UserSession;

import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * TODO Document
 */
public class SprintsResource extends CRUDResource<Sprint> {
	/**
	 * Constructs a new sprint resource.
	 * @param project project of the sprint
	 */
	public SprintsResource(Project project) {
		super(SqlobDAOFactory.getDAOUnderProject(Sprint.class, project));

		setAuthorizers(Authorizers.onlyUsers(project), SqlobDAOFactory.getDAOUnderProject(UserSession.class, project));
	}

	@Override
	protected Condition parseQuery(MultivaluedMap<String, String> params) {
		String sprintNumber = params.getFirst("sprintNumber");
		if (sprintNumber != null) {
			return new Condition("sprintNumber", "=", Integer.valueOf(sprintNumber));
		} else return null;
	}
}
