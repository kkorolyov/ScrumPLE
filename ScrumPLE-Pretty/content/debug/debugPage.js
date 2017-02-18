"use strict";

window.addEventListener('load', init);

function init() {
	document.getElementsByTagName('body')[0].appendChild(
		createEntryBox('projectsBox', "Projects", "projects", {
			name: "",
			description: "",
			visible: true,
			owner: {
				credentials: {
					handle: "",
					password: ""
				},
				displayName: "",
				role: ""
			}
		}));
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


function createButton(name, action) {
	var button = document.createElement('button');
	button.setAttribute('type', 'button')
	button.addEventListener('click', function(event) { if (event.target === this) action() });
	button.appendChild(document.createTextNode(name));

	return button;
}

/**
 * Creates and returns a new entry box.
 * 
 * @param {string} className class identifier
 * @param {string} title displayed title
 * @param {string} url url to box actions
 * @param {object} properties {name, input type} pairs defining the objects created by this box
 * @returns
 */
function createEntryBox(className, title, url, properties) {
	var box = document.importNode(
						document.getElementById('entryBox').content.querySelector('.entryBox'), true);	// Get live clone
	
	box.className += " " + className;
	box.addEventListener('click', function(event) {
		if (event.target === this) {
			var list = box.getElementsByClassName('entryList')[0];
			list.textContent = "Retrieving " + title + "...";

			ajax('GET', url, null, response => {
				displayRaw(response);

				list.textContent = "";

				if (typeof response === 'object') {
					for (var key in response) {
						list.appendChild(createEntry(title + ": " + key, url + "/" + key, response[key]));
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

	var createFormFieldset = createForm.elements['root'];
	formify(createFormFieldset, properties);

	createForm.elements['submitJson'].addEventListener('click', event => {
		ajax('POST', url, JSON.stringify(objectify(createFormFieldset)), response => { displayRaw(response) });
		createForm.reset();
	});
	return box;
}
/**
 * Constructs and returns a new entry.
 * 
 * @param {string} name entry name
 * @param {string} url url to entry actions
 * @param {object} object object to transform to entry
 * @returns
 */
function createEntry(name, url, object) {
	var entry = document.importNode(
							document.getElementById('entry').content.querySelector('.entry'), true);	// Get live clone
	entry.getElementsByClassName('title')[0].textContent = name;
	entry.getElementsByClassName('direct')[0].href = getUrl(url);

	var updateForm = entry.getElementsByClassName('updateForm')[0];
	var updateFormFieldset = updateForm.elements['root'];
	formify(updateFormFieldset, object);

	updateForm.elements['submit'].addEventListener('click', event => {
		ajax('PUT', url, JSON.stringify(objectify(updateFormFieldset)), response => { displayRaw(response) });
	});
	entry.getElementsByClassName('delete')[0].addEventListener('click', () => {
		ajax('DELETE', url, null, response => displayRaw(response));
	});
	return entry;
}

/**
 * Transforms an object into a set of fields within a fieldset.
 * 
 * @param {Node} fieldset fieldset to apply object to
 * @param {object} object object to apply
 */
function formify(fieldset, object) {
	for (var property in object) {
		if (typeof object[property] === 'object') {
			var nextForm = fieldset.appendChild(document.createElement('fieldset'));
			nextForm.name = property;	// ID new form by property name
			nextForm.appendChild(document.createElement('legend')).textContent = property;

			formify(nextForm, object[property]);	// Apply inner object's properties to new form
		} else {
			fieldset.appendChild(document.createTextNode(property + ": "));

			var input = fieldset.appendChild(document.createElement('input'));
			input.name = property;
			input.defaultValue = object[property];
			input.type = (value => {
				switch (typeof value) {
					case 'string': return 'text';
					case 'boolean': 
						input.checked = value;	// Hack for checkboxes
						return 'checkbox';
					default: return null;
				}
			})(object[property]);
		}
		fieldset.appendChild(document.createElement('br'));
	}
}
/**
 * Transforms a fieldset into an object.
 * 
 * @param {Node} fieldset fieldset with fieldset or input elements to transform to object
 * @returns
 */
function objectify(fieldset) {
	var object = {};

	for (var i = 0; i < fieldset.childNodes.length; i++) {
		var childNode = fieldset.childNodes[i];
		switch (childNode.tagName) {
			case 'FIELDSET':
				object[childNode.name] = objectify(childNode)
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
