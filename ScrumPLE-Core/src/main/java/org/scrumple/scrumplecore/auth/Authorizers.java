package org.scrumple.scrumplecore.auth;

import dev.kkorolyov.sqlob.persistence.Condition;
import org.scrumple.scrumplecore.scrum.User;
import org.scrumple.scrumplecore.database.DAO;

/**
 * A collection of basic {@link Authorizer} implementations.
 */
public class Authorizers {
	/** An authorizer which returns {@code true} for all REST methods. */
	public static Authorizer ALWAYS = new Authorizer() {
		@Override
		public boolean canGET(Credentials credentials) {
			return true;
		}
		@Override
		public boolean canPOST(Credentials credentials) {
			return true;
		}
		@Override
		public boolean canPUT(Credentials credentials) {
			return true;
		}
		@Override
		public boolean canDELETE(Credentials credentials) {
			return true;
		}
	};
	/** An authorizer which returns {@code false} for all REST methods. */
	public static Authorizer NEVER = new Authorizer() {
		@Override
		public boolean canGET(Credentials credentials) {
			return false;
		}
		@Override
		public boolean canPOST(Credentials credentials) {
			return false;
		}
		@Override
		public boolean canPUT(Credentials credentials) {
			return false;
		}
		@Override
		public boolean canDELETE(Credentials credentials) {
			return false;
		}
	};

	/**
	 * Constructs an authorizer which allows only credentials found in a collection of users.
	 * @param users user collection defining credentials to allow
	 * @return authorizer allowing only credentials matching {@code users}
	 */
	public static Authorizer onlyUsersInDAO(DAO<User> users) {
		return new Authorizer() {
			@Override
			public boolean canGET(Credentials credentials) {
				return credentialsInProject(credentials);
			}
			@Override
			public boolean canPOST(Credentials credentials) {
				return credentialsInProject(credentials);
			}
			@Override
			public boolean canPUT(Credentials credentials) {
				return credentialsInProject(credentials);
			}
			@Override
			public boolean canDELETE(Credentials credentials) {
				return credentialsInProject(credentials);
			}

			private boolean credentialsInProject(Credentials credentials) {
				return !users.get(new Condition("credentials", "=", credentials)).isEmpty();
			}
		};
	}
}
