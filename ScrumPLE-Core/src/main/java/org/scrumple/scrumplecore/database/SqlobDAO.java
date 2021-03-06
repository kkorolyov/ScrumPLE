package org.scrumple.scrumplecore.database;

import static org.scrumple.scrumplecore.assets.Assets.SYSTEM_DB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;
import javax.sql.DataSource;

import org.scrumple.scrumplecore.assets.Assets;

import dev.kkorolyov.simplelogs.Level;
import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.format.Formatters;
import dev.kkorolyov.sqlob.Session;
import dev.kkorolyov.sqlob.utility.Condition;

/**
 * {@code DAO} implementation for generic objects using {@code SQLOb} for persistence.
 * @param <T> persisted object type
 */
public class SqlobDAO<T> implements DAO<T> {
	private static final Logger log = Logger.getLogger(Level.DEBUG, Formatters.simple());

	private final Class<T> c;
	private final DataSource ds;

	/**
	 * Constructs a new instance with a datasource to the default {@code SYSTEM_DB}.
	 * @param c type of objects this DAO manages
	 */
	public SqlobDAO(Class<T> c) {
		this(c, DataSourcePool.get(Assets.get(SYSTEM_DB)));
	}
	/**
	 * Constructs a new instance with a custom datasource.
	 * @param c type of objects this DAO manages
	 * @param ds datasource
	 */
	public SqlobDAO(Class<T> c, DataSource ds) {
		this.c = c;
		this.ds = ds;
	}
	
	/**
	 * @param cond condition to check
	 * @return {@code true} if a persisted object matching {@code cond} exists
	 * @throws DataAccessException if an issue occurs during data access
	 */
	public boolean contains(Condition cond){
		return !get(cond).isEmpty();
	}
	
	/**
	 * Creates a new database.
	 * @param dbName database name
	 * @throws DataAccessException if an issue occurs during data access
	 */
	public void createDatabase(String dbName){
		update("CREATE DATABASE " + dbName);
	}
	/**
	 * Drops a database.
	 * @param dbName database name
	 * @throws DataAccessException if an issue occurs during data access
	 */
	public void dropDatabase(String dbName){
		update("DROP DATABASE " + dbName);
	}
	private void update(String statement) {
		try (Connection conn = ds.getConnection()) {
			conn.createStatement().executeUpdate(statement);
		} catch (SQLException e) {
			log.exception(e);
			throw new DataAccessException(e);
		}
	}

	@Override
	public boolean contains(T obj) {
		try (Session s = new Session(ds)) {
			return s.getId(obj) != null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T get(UUID id){
		try (Session s = new Session(ds)) {
			T result = s.get(c, id);

			if (result == null)
				throw new EntityNotFoundException("Entity not found: " + c.getSimpleName() + " " + id);

			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public Map<UUID, T> get(Condition cond){
		try (Session s = new Session(ds)) {
			return s.get(c, cond);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public Map<UUID, T> getAll(){
		return get((Condition) null);
	}
	
	@Override
	public void update(UUID id, T newObj){
		try (Session s = new Session(ds)) {
			if (s.get(c, id) == null)
				throw new EntityNotFoundException("Entity not found: " + c.getSimpleName() + " " + id);

			s.put(id, newObj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public UUID add(T obj){
		try (Session s = new Session(ds)) {
			return s.put(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public T remove(UUID id){
		try (Session s = new Session(ds)) {
			T result = s.get(c, id);
			if (result == null)
				throw new EntityNotFoundException("Entity not found: " + c.getSimpleName() + " " + id);

			s.drop(c, id);

			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
