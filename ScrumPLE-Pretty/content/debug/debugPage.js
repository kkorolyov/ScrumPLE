"use strict";

var restRoot = "https://ec2-52-10-231-227.us-west-2.compute.amazonaws.com:8443/scrumple/rest/";

populateRestRoots();

function populateRestRoots() {
	var elements = document.getElementsByClassName('missingRestRoot');
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

var credentials = null;
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
 * Displays raw server response in 'raw' element.
 * @param (object) response - Response to display
 */
function displayRaw(response) {	// For debug
	document.getElementById('raw').innerHTML = JSON.stringify(response, null, 2);
}

/**
 * Creates and returns a custom, empty 'div' meant to encapsulate a single entry.
 * @param (string) name - Entry name
 * @param (object) properties - Entry properties
 */
function createEntry(name, properties) {
	var entry = document.createElement('div');
	entry.setAttribute('class', "entry round");
	entry.setAttribute('title', "Click to expand");
	entry.appendChild(document.createElement('h4')).appendChild(document.createTextNode(name));
	
	var attributes = entry.appendChild(document.createElement('p'));
	for (var name in properties) {
		attributes.appendChild(document.createTextNode(name + "=" + properties[name]));
		attributes.appendChild(document.createElement('br'));
	}
	return entry;
}
function createButton(name, action) {
	var button = entry.appendChild(document.createElement('button'));
	button.setAttribute('type', 'button')
	button.setAttribute('onclick', action);
	button.appendChild(document.createTextNode(name));
}

/** Retrieves and displays all projects in the 'projectList' element. */
function showProjects() {
	var projectsList = document.getElementById('projectsList');
	projectsList.innerHTML = "Getting Projects...";
	
	var url = "projects";
	ajax("GET", url, null, function(projects) {
		displayRaw(projects);
		
		projectsList.innerHTML = "";
		
		for (var key in projects) {
			var entry = projectsList.appendChild(createEntry("Project: " + key, projects[key]));
			entry.setAttribute('onclick', "showUsers('" + key + "', usersList)");
			
			entry.appendChild(createButton("DELETE", "ajax('DELETE', '" + url + "/" + key + "', null, function(response) {displayRaw(response);});"));
		}
	});
}
function createProject(name, description, isPrivate) {
	var project = {"name": name,
			"description": description,
			"isPrivate": isPrivate};
	
	ajax("POST", "projects", JSON.stringify(project), function(response) {
		displayRaw(response);
	});
}
function deleteProject(projectId) {
	ajax("DELETE", "projects/" + projectId, null, function(response) {
		displayRaw(response);
	});
}

/**
 * Retrieves and displays all users under a project in the 'usersList' element.
 * @param (string) projectId - ID of project owning users
 */
function showUsers(projectId)
	var usersList = document.getElementById('usersList');
	usersList.innerHTML = "Getting users for project: " + projectId + "...";
	
	var url = "projects/" + projectId + "/users";
	ajax("GET", url, null, function(users) {
		displayRaw(users);
		
		usersList.innerHTML = "Project: " + projectId;
		
		for (var key in users) {
			var entry = usersList.appendChild(createEntry("User: " + key, users[key]));
			entry.appendChild(createButton("DELETE", "ajax('DELETE', '" + url + "/" + key + "', null, function(response) {displayRaw(response);});"));
		}
	});
}
function createUser(projectId, handle, password) {
	
}
function deleteUser(projectId, userId) {
	
}
