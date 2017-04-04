"use strict"

angular
	.module('resources', ['rest'])
	.factory('resources', ['$q', 'rest', function ($q, rest) {
		return function () {	// Closure for private fields
			let _project = {}	// Current project
			let _user = null // Current user

			function _projectUrl() { return "projects/" + _project.id }

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
				 * @returns {Object} promise resolving to null but allowing for no-arg then() chaining
				 */
				login: function (handle, password) {
					return rest.login(handle, password, this.project().id)
						.then(user => {	// Success
							_user = user
							_user.current = true

							// Get logged-in user ID
							rest.ajax('GET', _projectUrl() + "/users?displayName=" + _user.displayName)
								.then(userMap => {
									_user.id = Object.keys(userMap)[0]
								})

							// Load logged-in project properties
							rest.ajax('GET', _projectUrl())
								.then(project => {
									for (let property in project) _project[property] = project[property]
								})
							return null
						}, reason => {	// Failure
							_user = null

							return $q.reject(reason)
						})
				},
				logout: () => {
					//_project = {}	Avoid removing project ID for now
					_user = null

					rest.logout()
				},

				/** @returns {Object} promise resolving to UUID -> User map of all users in current project */
				users: function () {
					return rest.ajax('GET', _projectUrl() + "/users")
						.then(users => {
							return users
						}, reason => {
							return $q.reject(reason)
						})
				},

				/** @returns {Object} promise resolving to UUID -> Meeting map of all users in current project */
				meetings: function () {
					return rest.ajax('GET', _projectUrl() + "/meetings")
						.then(meetings => {
							return meetings
						}, reason => {
							return $q.reject(reason)
						})
				},

				sprints: function () {
					return rest.ajax('GET', _projectUrl() + "/sprints")
						.then(sprints => {
							return sprints
						}, reason => {
							return $q.reject(reason)
						})
				},

				stories: function() {
					return rest.ajax('GET', _projectUrl() + "/stories")
						.then(stories => {
							return stories
						}, reason => {
							return $q.reject(reason)
						})
				},

				createStory: function(story, storyPoint) {
					const newStory = {story: story, storyPoint: storyPoint}
					return rest.ajax('POST', _projectUrl() + "/stories", newStory)
				}
			}
		}()
	}])
