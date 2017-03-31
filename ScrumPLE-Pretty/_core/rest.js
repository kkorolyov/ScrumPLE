"use strict"

const rest = {
	token: null,
	login: function(handle, password, projectId) {
		let credentials = {
			"handle": handle,
			"password": password
		}
		this.ajax('POST', "projects/" + projectId + "/auth", JSON.stringify(credentials), response => {
			console.log(response)
			this.token = response
		})
		console.log(projectId + "/auth")
	},
	getUrl: function(url) {
		return "https://ec2-52-10-231-227.us-west-2.compute.amazonaws.com:8443/scrumple/rest/" + url
	},
	/**
	 * Sends a REST request.
	 * 
	 * @param {string} method request method
	 * @param {string} url request URL starting after REST root
	 * @param {Object} [content] request content
	 * @param {function(string)} [responseHandler] invoked after server responds
	 */
	ajax: function(method, url, content, responseHandler) {
		const xhttp = new XMLHttpRequest()

		if (responseHandler) {
			xhttp.onreadystatechange = function () {
				if (this.readyState === 4) {
					let response = this.responseText
					try {
						response = JSON.parse(response)
					} catch (e) {
						console.log(e.message)
					}
					responseHandler(response)
				}
			}
		}
		xhttp.open(method, this.getUrl(url), true)

		if (this.token) xhttp.setRequestHeader("Authorization", this.token)
		if (content) xhttp.setRequestHeader("Content-type", "application/json")

		xhttp.send(content)
	}
}
