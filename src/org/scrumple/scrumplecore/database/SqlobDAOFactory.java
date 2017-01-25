package org.scrumple.scrumplecore.database;

import static org.scrumple.scrumplecore.assets.Assets.SYSTEM_DB;

import java.util.UUID;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.scrumple.scrumplecore.applications.Project;
import org.scrumple.scrumplecore.assets.Assets;

import dev.kkorolyov.sqlob.persistence.Condition;

/**
 * Provides custom {@link SqlobDAO} implementations.
 */
public final class SqlobDAOFactory {
	/** @return SQLOb DAO for {@code Project} objects */
	public static DAO<Project> getProjectDAO() throws DataAccessException {
		return new SqlobDAO<Project>(Project.class, DataSourcePool.get(Assets.get(SYSTEM_DB))) {
			@Override
			public UUID add(Project obj) {
				String projectName = obj.getName().replaceAll("\\s+", "_");
				obj.setName(projectName);
				
				if (contains(new Condition("name", "=", projectName)))
					throw new EntityExistsException("A project of the same name already exists: " + projectName);
				
				createDatabase(projectName);
				
				return super.add(obj);
			}
			
			@Override
			public Project remove(UUID id) throws EntityNotFoundException, DataAccessException {
				Project result = super.remove(id);
				dropDatabase(result.getName());
				
				return result;
			}
		};
	}
}
