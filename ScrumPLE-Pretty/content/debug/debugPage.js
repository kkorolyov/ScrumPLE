"use strict";

window.addEventListener('load', applyEventListeners);

function applyEventListeners() {
	document.getElementById('debugResetButton').addEventListener('click', function(event) {
		if (event.target === this) {
			debugReset();
			event.stopPropagation();
		}
	})
	document.getElementById('projectsBox').addEventListener('click', function(event) {
		if (event.target === this) {
			showProjects();
			event.stopPropagation();
		}
	});
}

/**
 * Displays raw server response in 'raw' element.
 * @param (object) response - Response to display
 */
function displayRaw(response) {	// For debug
	document.getElementById('raw').innerHTML = JSON.stringify(response, null, 2);
}

function debugReset() {
	ajax("GET", "debug/reset", null, function(response) {displayRaw(response)});
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
	button.addEventListener('click', function(event) {
		if (event.target === this) {
			action();
			event.stopPropagation();
		}
	});
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
			(function(url, key) {
				var entry = projectsList.appendChild(createEntry("Project: " + key, projects[key]));
				entry.addEventListener('click', function(event) {
					showUsers(key);
					event.stopPropagation();
				});
				entry.appendChild(createButton("DELETE", function() {
					ajax('DELETE', url + "/" + key, null, function(response) {
						displayRaw(response);
					});
				}));
			})(url, key);
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
			(function(url, key) {
				var entry = usersList.appendChild(createEntry("User: " + key, users[key]));
				entry.appendChild(createButton("DELETE", function() {
					ajax('DELETE', url + "/" + key, null, function(response) {
						displayRaw(response);
					});
				}));
			})(url, key);
		}
	});
}
function createUser(projectId, handle, password) {
	var user = {}
}
function deleteUser(projectId, userId) {

}
