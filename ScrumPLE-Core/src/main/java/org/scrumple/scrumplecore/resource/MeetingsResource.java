package org.scrumple.scrumplecore.resource;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.scrumple.scrumplecore.auth.Authorizers;
import org.scrumple.scrumplecore.database.SqlobDAOFactory;
import org.scrumple.scrumplecore.scrum.Meeting;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.auth.UserSession;

import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * Provides endpoints for accessing a project's meetings.
 */
public class MeetingsResource extends CRUDResource<Meeting> {
	/**
	 * Constructs a new meetings resource.
	 * @param project meetings scope
	 */
	public MeetingsResource(Project project) {
		super(SqlobDAOFactory.getDAOUnderProject(Meeting.class, project));

		setAuthorizers(Authorizers.onlyUsers(project), SqlobDAOFactory.getDAOUnderProject(UserSession.class, project));
	}

	/**
	 * Clones a meeting.
	 * @param id      ID of meeting to clone
	 * @param start   clone's start time in millis since epoch start
	 * @param headers request's HTTP headers
	 * @return clone's ID
	 */
	@POST
	@Path("{uuid}")
	public UUID clone(@PathParam("uuid") UUID id, long start, @Context HttpHeaders headers) {
		Meeting source = retrieve(id, headers);
		Meeting clone = new Meeting(source, start);

		return create(clone, headers);
	}

	@Override
	protected Condition parseQuery(MultivaluedMap<String, String> queryParams) {
		String startString = queryParams.getFirst("start");
		String endString = queryParams.getFirst("end");

		Instant start = (startString == null) ? null : Instant.ofEpochMilli(Long.parseLong(startString));
		Instant end = (endString == null) ? null : Instant.ofEpochMilli(Long.parseLong(endString));

		Condition condition = (start == null) ? null : new Condition("start", ">=", Timestamp.from(start));
		if (end != null) {
			Condition endCondition = new Condition("start", "<=", Timestamp.from(end));

			if (condition == null) condition = endCondition;
			else condition.and(endCondition);
		}
		return condition;
	}
}
