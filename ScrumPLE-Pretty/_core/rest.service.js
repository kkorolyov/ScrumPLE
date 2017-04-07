"use strict"

angular
	.module('rest', [])
	.factory('rest', ['$http', '$q', function ($http, $q) {
		return function () {	// Closure for private fields
			let _session = null

			return {
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
					const credentials = {
						"handle": handle,
						"password": password
					}
					return this.ajax('POST', "projects/" + projectId + "/auth", credentials)
						.then(response => {
							_session = response

							return _session.user
						}, reason => {
							return $q.reject(reason)
						})
				},
				logout: function () {
					_session = null
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
						headers: { 'Content-Type': 'application/json' },
						//cache: true
					}
					if (_session) httpConf.headers['Authorization'] = _session.token

					if (content) httpConf.data = content

					console.log("Sending request: " + JSON.stringify(httpConf))
					return $http(httpConf)
						.then(function (response) {
							const result = response.data

							console.log("Success: " + JSON.stringify(result))
							return result
						}, function (response) {
							const reason = response.status + ": " + response.data

							console.log(reason)
							return $q.reject(reason)
						})
				}
			}
		}()
	}])
