"use strict"

angular
	.module('rest', ['storage'])
	.factory('rest', ['$http', '$q', 'storage', function ($http, $q, storage) {
		return function () {	// Closure for private fields
			/**
			 * Gets or sets auth.
			 * @param {Object} [auth] auth to set
			 * @returns {Object} current auth if invoked without an argument
			 */
			function _auth(auth) {
				return storage.storage("auth", auth)
			}
			/**
			 * Gets or sets session.
			 * @param {Object} [session] session to set
			 * @returns {Object} current session if invoked without an argument
			 */
			function _session(session) {
				return storage.storage("sess", session)
			}

			const _retry = function () {
				const defaultRetries = 1
				let retries = defaultRetries

				return () => {
					if (retries > 0) {
						retries--
						return true
					} else {
						retries = defaultRetries
						return false
					}
				}
			}()

			return {
				auth: function () {
					return _auth() != null
				},

				getUrl: function (url) {
					return "http://ec2-52-10-231-227.us-west-2.compute.amazonaws.com:8080/scrumple/rest/" + url
				},

				/**
				 * Logs in with a handle-password pair to a project.
				 * @param {string} handle login handle
				 * @param {string} password login password
				 * @param {string} projectId ID of project to log in to
				 * @returns {Object} promise resolving to authenticated user
				 */
				login: function (handle, password, projectId) {
					if (handle) {
						_auth({
							credentials: {
								handle: handle,
								password: password
							},
							projectId: projectId
						})
					}
					const auth = _auth()

					return this.ajax('POST', "projects/" + auth.projectId + "/auth", auth.credentials)
						.then(response => {
							_session(response)

							return response.user
						}, reason => {
							this.logout()

							return $q.reject(reason)
						})
				},
				logout: function () {
					_auth(null)
					_session(null)
				},

				/**
				 * Invoked when an async request succeeds.
				 * @callback successCB
				 * @param {string|Object} response response text or object
				 */
				/**
				 * Invoked when an async request errors.
				 * @callback errorCB
				 * @param {string|Object} response response text or object
				 */
				/**
				 * Sends a REST request.
				 * @param {string} method request method
				 * @param {string} url request URL starting after REST root
				 * @param {Object} [content] request content
				 * @returns {Object} promise resolving to response data
				 */
				ajax: function (method, url, content) {	// TODO Handle re-auth
					const httpConf = {
						method: method,
						url: this.getUrl(url),
						headers: { 'Content-Type': 'application/json' }
					}
					if (_session()) httpConf.headers['Authorization'] = _session().token

					if (content) httpConf.data = content

					console.log("Sending request: " + JSON.stringify(httpConf))
					return $http(httpConf)
						.then(response => {
							const result = response.data

							console.log("Success: " + JSON.stringify(result))
							return result
						}, response => {
							if (response.status === 401 && _retry()) {
								console.log("Unauthorized, attempting to re-auth...")

								this.login()	// Login with stored auth
									.then(() => {
										console.log("Re-auth successful, retrying ajax...")
										
										return this.ajax(method, url, content)
									}, reason => {
										console.log("Re-auth attempt failed")
										this.logout()
										
										return $q.reject(reason)
									})
							}
							const reason = response.status + ": " + response.data

							console.log(reason)
							return $q.reject(reason)
						})
				}
			}
		}()
	}])
