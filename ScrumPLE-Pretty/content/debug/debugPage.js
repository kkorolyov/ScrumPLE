"use strict";

window.addEventListener('load', init);

function init() {
	var projectsBox = createEntryBox('projectsBox', "Projects", "projects", {
		name: 'text',
		description: 'text',
		visible: 'checkbox',
		owner: {
			credentials: {
				handle: 'text',
				password: 'password'
			},
			displayName: 'text',
			role: 'text'
		}
	});
	applyEventListeners();
}
function applyEventListeners() {
	document.getElementById('debugReset').addEventListener('click', function(event) {
		if (event.target === this) debugReset();
	})
}

/**
 * Displays raw server response in 'raw' element.
 * @param (object) response - Response to display
 */
function displayRaw(response) {	// For debug
	document.getElementById('raw').innerHTML = (typeof response === 'string') ? response : JSON.stringify(response, null, 2);
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

/**
 * Creates a new entry box at the end of document.
 * Returns the created entry box.
 * @param {string} className class identifier
 * @param {string} title displayed title
 * @param {string} url url to box action
 * @param {Object} properties {name, input type} pairs defining the objects created by this box
 * @returns
 */
function createEntryBox(className, title, url, properties) {
	var box = document.getElementsByTagName('body')[0].appendChild(
						document.importNode(
						document.getElementById('entryBox').content.querySelector('.entryBox'), true));	// Import, append, assign live node
	
	box.className += " " + className;
	box.addEventListener('click', function(event) {
		if (event.target === this) {
			var list = box.getElementsByClassName('entryList')[0];
			list.textContent = "Retrieving " + title + "...";

			ajax('GET', url, null, response => {
				list.textContent = "";

				displayRaw(response);

				if (typeof response === 'object') {
					for (var key in response) {
						(function(key) {
							var entry = list.appendChild(createEntry(title + ": " + key, response[key]));
							entry.appendChild(createButton("DELETE", () => {
								ajax('DELETE', url + "/" + key, null, response => displayRaw(response));
							}));
						})(key);
					}
				}
			});
		}
	});
	box.getElementsByClassName('title')[0].textContent = title;
	box.getElementsByClassName('direct')[0].href = getUrl(url);

	var createForm = box.getElementsByClassName('createForm')[0];
	createForm.action = getUrl(url);
	createForm.method = 'POST';

	var createFormFieldset = createForm.getElementsByTagName('fieldset')[0];
	appendFields(createFormFieldset, properties);

	var submitForm = createFormFieldset.appendChild(document.createElement('input'));
	submitForm.type = 'submit';
	submitForm.name = 'submitForm';
	submitForm.value = "Submit (Form)";

	var submitJson = createFormFieldset.appendChild(document.createElement('input'));
	submitJson.type = 'button';
	submitJson.name = 'submitJson';
	submitJson.value = "Submit (JSON)";
	submitJson.addEventListener('click', function(event) {
		var object = formToObject(createFormFieldset);
		ajax('POST', url, JSON.stringify(object), function(response) { displayRaw(response) });
	});
	return box;
}
/**
 * Appends all fields in an object to a fieldset.
 * 
 * @param {Node} fieldset element to append to
 * @param {Object} object object with fields to append
 */
function appendFields(fieldset, object) {
	for (var property in object) {
		if (typeof object[property] === 'object') {
			var nextForm = fieldset.appendChild(document.createElement('fieldset'));
			nextForm.name = property;	// ID new form by property name
			nextForm.appendChild(document.createElement('legend')).textContent = property;

			appendFields(nextForm, object[property]);	// Apply inner object's properties to new form
		} else {
			fieldset.appendChild(document.createTextNode(property + ": "));

			var input = fieldset.appendChild(document.createElement('input'));
			input.name = property;
			input.type = object[property];

		}
		fieldset.appendChild(document.createElement('br'));
	}
}
/**
 * Constructs an object from fieldset inputs.
 * 
 * @param {Node} fieldset node containing 'input' elements
 */
function formToObject(fieldset) {
	var object = {};

	for (var i = 0; i < fieldset.childNodes.length; i++) {
		var childNode = fieldset.childNodes[i];
		switch (childNode.tagName) {
			case 'FIELDSET':
				object[childNode.name] = formToObject(childNode)
				break;
			case 'INPUT':
				switch (childNode.type) {
					case 'text':
					case 'password':
						object[childNode.name] = childNode.value;
						break;
					case 'checkbox':
						object[childNode.name] = childNode.checked;
						break;
				}
				break;
		}
	}
	return object;
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
