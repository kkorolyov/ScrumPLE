var restRoot = "https://ec2-52-10-231-227.us-west-2.compute.amazonaws.com:8443/scrumple/rest/";
var credentials = null;

/**
 * Runs a request.
 * @param (string) method - Request method
 * @param (string) url - Partial request URL after the 'restRoot' global
 * @param (string) content - Optional request content
 * @param (function(response)) responseHandler - Function invoked after server responds
 */
function ajax(method, url, content, responseHandler) {	// Main REST invocation
	var xhttp = new XMLHttpRequest();
	
	if (responseHandler != null) {
		xhttp.onreadystatechange = function() {
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
	xhttp.open(method, restRoot + url, true);
	
	if (credentials != null) {
		xhttp.setRequestHeader("Authorization", "Basic " + credentials);
	}
	if (content != null) {
		xhttp.setRequestHeader("Content-type", "application/json");
	}
	xhttp.send(content);
}

/**
 * Changes the current value of the 'credentials' global.
 * Credentials are combined into a single string (HANDLE:PASSWORD) and Base64 encoded.
 * @param (string) handle - User handle
 * @param (string) password - User password
 */
function login(handle, password) {
	credentials = btoa(handle + ":" + password);
	console.log("Logged in as: " + handle);
}

/**
 * Prepends the 'restRoot' global to the link attribute of a class of elements.
 * @param (string) className - Name of the class defining elements to invoke this function on
 */
function populateRestRoots(className) {
	var elements = document.getElementsByClassName(className);
	for (var i = 0; i < elements.length; i++) {
		var element = elements[i];
		switch (element.nodeName) {
			case 'A':
				element.setAttribute('href', restRoot + element.getAttribute('href'));
				break;
			case 'FORM':
				element.setAttribute('action', restRoot + element.getAttribute('action'));
				break;
		}
	}
}
