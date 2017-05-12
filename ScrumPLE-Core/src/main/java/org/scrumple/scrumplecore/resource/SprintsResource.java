package org.scrumple.scrumplecore.resource;

import javax.ws.rs.core.MultivaluedMap;
import java.sql.Timestamp;
import java.time.Instant;
import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.Sprint;
import org.scrumple.scrumplecore.auth.UserSession;

import dev.kkorolyov.sqlob.utility.Condition;

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
	protected Condition parseQuery(MultivaluedMap<String, String> queryParams) {
		String stringDate = queryParams.getFirst("date");
		
		Timestamp date = (stringDate == null) ? null : Timestamp.from(Instant.ofEpochMilli(Long.parseLong(stringDate)));

		Condition condition = (date == null) ? null : new Condition("start", ">=", date).and("end", "<=", date);
	
		return condition;
		
	}
}
