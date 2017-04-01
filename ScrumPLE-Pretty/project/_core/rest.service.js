"use strict"

angular
	.module('rest', [])
	.factory('rest', ['$http', function ($http) {
		return {
			getUrl: function (url) {
				return "http://ec2-52-10-231-227.us-west-2.compute.amazonaws.com:8080/scrumple/rest/" + url
			},
			token: null,
			/**
			 * Logs in with a handle-password pair to a project.
			 * @param {string} handle login handle
			 * @param {string} password login password
			 * @param {string} projectId ID of project to log in to
			 * @param {function} [success] invoked upon authentication success
			 * @param {function} [failure] invoked upon authentication failure
			 */
			login: function(handle, password, projectId, success, failure) {
				const credentials = {
					"handle": handle,
					"password": password
				}
				this.ajax('POST', "projects/" + projectId + "/auth", credentials, response => {
					this.token = response.token

					if (success) success()
				}, response => {
					if (failure) failure()
				})
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
			 * @param {successCB} [success] invoked upon request success
			 * @param {errorCB} [error] invoked upon request error
			 */
			ajax: function(method, url, content, success, error) {
				const httpConf = {
					method: method,
					url: this.getUrl(url),
					headers: {'Content-Type': 'application/json'}
				}
				if (this.token) httpConf.headers['Authorization'] = this.token
				
				if (content) httpConf.data = content

				console.log("Sending request: " + JSON.stringify(httpConf))
				return $http(httpConf).then(function (response) {
					if (success) success(response.data)

					console.log("Success: " + JSON.stringify(response.data))
				}, function (response) {
					if (error) error(response.data)
					
					console.log("Error: " + JSON.stringify(response.data))
				})
			}
		}
	}])
