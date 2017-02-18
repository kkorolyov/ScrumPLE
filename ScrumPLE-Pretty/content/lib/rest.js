var rest = {
	credentials: null,
	login: function(handle, password) {
		this.credentials = btoa(handle + ":" + password);
	},
	getUrl: function(url) {
		return "https://ec2-52-10-231-227.us-west-2.compute.amazonaws.com:8443/scrumple/rest/" + url;
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
		var xhttp = new XMLHttpRequest();

		if (responseHandler) {
			xhttp.onreadystatechange = function () {
				if (this.readyState === 4) {
					var response = this.responseText;
					try {
						response = JSON.parse(response);
					} catch (e) {
						console.log(e.message);
					}
					responseHandler(response);
				}
			}
		}
		xhttp.open(method, this.getUrl(url), true);

		if (this.credentials) xhttp.setRequestHeader("Authorization", "Basic " + this.credentials);
		if (content) xhttp.setRequestHeader("Content-type", "application/json");

		xhttp.send(content);
	}
}
