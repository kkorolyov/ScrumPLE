"use strict"

angular
	.module('resources', ['rest'])
	.factory('resources', ['rest', function (rest) {
		return function () {	// Closure for private fields
			let _project = {}	// Current project
			let _user = null // Current user

			return {
				/** @returns {Object} current project */
				project: () => { return _project },
				/** @returns {Object} current authenticated user, or null if not authentiated */
				user: () => { return _user },

				/** @returns {boolean} true if authenticated */
				auth: function () { return this.user() != null },	// For semantics

				/**
				 * Attempts to authenticate to the current project.
				 * @param {string} handle login handle
				 * @param {string} password login password
				 * @param {function} [success] invoked upon authentication success
				 * @param {function} [failure] invoked upon authentication failure
				 */
				login: function (handle, password, success, failure) {
					rest.login(handle, password, this.project().id,
						user => {	// Success
							_user = user

							rest.ajax('GET', "projects/" + this.project().id, null,
								project => {
									for (let property in project) this.project()[property] = project[property]
								})
							if (success) success()
						}, () => {	// Failure
							_user = null

							if (failure) failure()
						})
				},
				logout: () => {
					_user = null

					rest.token = null	// TODO Delegate to rest service
				}
			}
		}()
	}])
