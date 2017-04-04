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
				 * Sets the current project.
				 * @param {Object} project project to enter
				 */
				enter: function (project) {
					_project = project
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
							this.values(this.users(user.displayName))
								.then(users => {
									_user = users[0]
									_user.current = true
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

				/** 
				 * @param {string} [name] filters returned projects by LIKE name
				 * @returns {Object} promise resolving to UUID -> Project map of projects
				 */
				projects: function (name) {
					let url = "projects"
					if (name) url += ("?name=" + name)

					return rest.ajax('GET', url)
						.then(projects => {
							return projects
						}, reason => {
							return $q.reject(reason)
						})
				},

				/**
				 * @param {string} [name] filters returned users by displayName
				 * @returns {Object} promise resolving to UUID -> User map of all users in current project
				 */
				users: function (name) {
					let url = _projectUrl() + "/users"
					if (name) url += ("?displayName=" + name)

					return rest.ajax('GET', url)
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
				},

				/**
				 * Returns an array of resources with each ID injected into the resource itself.
				 * @param {Object} resources promise resolving to UUID -> Resource map
				 * @returns promise resolving to array of transformed resources
				 */
				values: function (resources) {
					return resources
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
				}
			}
		}()
	}])
