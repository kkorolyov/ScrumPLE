"use strict";

window.addEventListener('load', init);

function init() {
	var projectsBox = createEntryBox('projectsBox', "Projects", getUrl("projects"));
	console.log(projectsBox);
	projectsBox.addEventListener('click', function(event) {
		console.log("test " + this);
		if (event.target === this) showProjects();
	});
	applyEventListeners();
}
function applyEventListeners() {
	document.getElementById('debugResetButton').addEventListener('click', function(event) {
		if (event.target === this) debugReset();
	})
}
/**
 * Displays raw server response in 'raw' element.
 * @param (object) response - Response to display
 */
function displayRaw(response) {	// For debug
	document.getElementById('raw').innerHTML = JSON.stringify(response, null, 2);
}

function debugReset() {
	ajax("GET", "debug/reset", null, function(response) { displayRaw(response) });
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
	button.addEventListener('click', function(event) { if (event.target === this) action() });
	button.appendChild(document.createTextNode(name));

	return button;
}

function createEntryBox(id, title, direct) {
	var template = document.getElementById('entryBox').content,
	div = template.querySelectorAll('div')[0];
	div.setAttribute('id', id);	// Set inner div's ID
	console.log(div);

	template.getElementById('boxTitle').innerHTML = title;
	template.getElementById('boxDirect').href = direct;

	return document.getElementsByTagName('body')[0].appendChild(document.importNode(template.querySelectorAll('div')[0], true));
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
					if (event.target === this) {
						showUsers(key);
						showMeetings(key);
					}
				});
				entry.appendChild(createButton("DELETE", function() {
					ajax('DELETE', url + "/" + key, null, function(response) { displayRaw(response) });
				}));
			})(url, key);
		}
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
function showMeetings(projectId) {
	document.getElementById('meetingsBox').style.display = "block";

	var meetingsList = document.getElementById('meetingsList');
	meetingsList.innerHTML = "Getting meetings for project: " + projectId + "...";


}

function createUser(projectId, handle, password) {
	var user = {}
}
function deleteUser(projectId, userId) {

}
