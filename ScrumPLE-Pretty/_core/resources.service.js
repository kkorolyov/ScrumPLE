"use strict"

angular
	.module('resources', ['rest', 'storage'])
	.factory('resources', ['$q', 'rest', 'storage', function ($q, rest, storage) {
		return function () {	// Closure for private fields
			function _project(project) {
				return storage.storage("project", project)
			}
			function _user(user) {
				return storage.storage("user", user)
			}

			return {
				/** @returns {Object} current project */
				project: () => { return _project() },
				/** @returns {Object} URL to current project */
				projectUrl: () => { return "projects/" + _project().id },

				/** @returns {Object} current authenticated user, or null if not authentiated */
				user: () => { return _user() },
				/** @returns {boolean} true if authenticated */
				auth: ()  =>{ return rest.auth() },

				/**
				 * Sets the current project.
				 * @param {Object} project project to enter
				 */
				enter: function (project) {
					_project(project)
				},

				/**
				 * Attempts to authenticate to the current project.
				 * @param {string} handle login handle
				 * @param {string} password login password
				 * @returns {Object} promise resolving to null but allowing for no-arg then() chaining
				 */
				login: function (handle, password) {
					return rest.login(handle, password, this.project().id)
						.then(user => {	// Success
							// Save logged-in user
							this.get(this.projectUrl() + "/users", { displayName: user.displayName })
								.then(users => {
									_user(users[0])
									_user().current = true
								})
							return null
						}, reason => {	// Failure
							_user(null)

							return $q.reject(reason)
						})
				},
				logout: () => {
					_project(null)
					_user(null)

					rest.logout()
				},

				/**
				 * Retrieves a list of resources.
				 * @param {string} url url to retrieve from, starting after the REST root
				 * @param {Object} [filters] additional filter parameters as {name: stringValue} properties
				 * @returns {Object} promise resolving to list of resources with respective IDs added as properties
				 */
				get: function (url, filters) {
					let finalUrl = url
					if (filters) {
						finalUrl += "?"

						for (let filter in filters) finalUrl += (filter + "=" + filters[filter] + "&")	// Append all filters as query params
						finalUrl = finalUrl.slice(0, -1)	// Remove trailing "&"
					}
					return rest.ajax('GET', finalUrl)
						.then(results => {
							const list = []

							for (let key in results) {
								const resource = results[key]
								resource.id = key

								list.push(resource)
							}
							return list
						}, reason => {
							return $q.reject(reason)
						})
				},
				/**
				 * Adds or replaces a resources.
				 * @param {string} url resource url
				 * @param {Object} obj resource to add (if obj does not have an ID property) or replace (if obj has an ID property)
				 * @return {Object} promise resolving to ID of added resource, or null if replacing resource
				 */
				set: function (url, obj) {
					return rest.ajax(obj.id ? 'PUT' : 'POST', obj.id ? (url + "/" + obj.id) : url, obj)
						.then(newId => {
							return newId
						}, reason => {
							return $q.reject(reason)
						})
				},
				/**
				 * Deletes a resource.
				 * @param {string} url resource url
				 * @return {Object} promise resolving to null
				 */
				delete: function (url, obj) {
					return rest.ajax('DELETE', url + "/" + obj.id)
				}
			}
		}()
	}])
