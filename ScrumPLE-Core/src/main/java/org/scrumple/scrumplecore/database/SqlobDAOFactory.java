package org.scrumple.scrumplecore.database;

import static org.scrumple.scrumplecore.assets.Assets.SYSTEM_DB;

import java.util.UUID;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.scrumple.scrumplecore.assets.Assets;
import org.scrumple.scrumplecore.scrum.Project;
import org.scrumple.scrumplecore.scrum.User;

import dev.kkorolyov.sqlob.utility.Condition;

/**
 * Provides custom {@link SqlobDAO} implementations.
 */
public final class SqlobDAOFactory {
	/** @return SQLOb DAO for {@code Project} objects */
	public static DAO<Project> getProjectDAO() throws DataAccessException {
		return new SqlobDAO<Project>(Project.class, DataSourcePool.get(Assets.get(SYSTEM_DB))) {
			@Override
			public void update(UUID id, Project newObj) throws EntityNotFoundException, DataAccessException {
				if (!newObj.getName().equals(get(id).getName())) throw new IllegalArgumentException("Project name may not change");

				super.update(id, newObj);
			}

			@Override
			public UUID add(Project obj) {
				String projectName = obj.getName().replaceAll("\\s+", "_").replaceAll(";", "");
				obj.setName(projectName);
				
				if (contains(new Condition("name", "=", projectName)))
					throw new EntityExistsException("A project of the same name already exists: " + projectName);
				
				createDatabase(projectName);
				getDAOUnderProject(User.class, obj).add(obj.getOwner());
				
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
	
	/**
	 * @param c type of {@code DAO} to get
	 * @param project project hosting {@code DAO}
	 * @return appropriate {@code DAO}
	 */
	public static <T> DAO<T> getDAOUnderProject(Class<T> c, Project project) {
		return new SqlobDAO<T>(c, DataSourcePool.get(project.getName()));
	}
}
