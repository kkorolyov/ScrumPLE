"use strict";

window.onload = function() {
	populateRestRoots('missingRestRoot');
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
	var button = document.createElement('button');
	button.setAttribute('type', 'button')
	button.setAttribute('onclick', action);
	button.appendChild(document.createTextNode(name));
	
	return button;
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
			entry.setAttribute('onclick', "showUsers('" + key + "')");
			
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
function showUsers(projectId) {
	document.getElementById('usersBox').style.display = "block";

	var usersList = document.getElementById('usersList');
	usersList.innerHTML = "Getting users for project: " + projectId + "...";
	
	var url = "projects/" + projectId + "/users";
	ajax("GET", url, null, function(users) {
		displayRaw(users);
		
		document.getElementById('usersDirect').setAttribute('href', restRoot + url);
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
