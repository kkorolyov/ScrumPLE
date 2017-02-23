"use strict";

window.addEventListener('load', init);

function init() {
	var body = document.getElementsByTagName('body')[0];
	body.appendChild(createProjectsBox());
	body.appendChild(document.createElement('div')).id = 'selectionBox';

	applyEventListeners();
}

function createProjectsBox() {
	return createEntryBox('projectsBox', "Projects", "projects", {
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
	},
	(function() {
		var selections = [];
		function select(node) {
			for (var i = 0; i < selections.length; i++) {
				selections[i].classList.remove('selected');
			}
			node.classList.add('selected');
			selections.push(node);
		}
		return function(object, url) {
			select(this);

			var box = document.getElementById('selectionBox');
			while(box.firstChild) box.removeChild(box.firstChild);

			box.appendChild(createUsersBox(object.name, url));
			box.appendChild(createMeetingsBox(object.name, url));
		}
	})());
}
function createUsersBox(name, url) {
	return createEntryBox('usersBox', "Users: " + name, url + "/users", {
		credentials: {
			handle: "",
			password: ""
		},
		displayName: "",
		role: ""
	});
}
function createMeetingsBox(name, url) {
	return createEntryBox('meetingsBox', "Meetings: " + name, url + "/meetings", {
		type: "",
		start: new Date(),
		end: new Date()
	});
}

function applyEventListeners() {
	document.getElementById('debugReset').addEventListener('click', function(event) {
		if (event.target === this) {
			var oldCredentials = rest.credentials;
			rest.login("d@bugg3r", "d3bug1t!");

			rest.ajax("GET", "debug/reset", null, response => displayRaw(response));

			rest.credentials = oldCredentials;
		}
	});
	document.getElementById('loginForm').addEventListener('submit', function(event) {
		event.preventDefault();
		rest.login(this.elements['handle'].value, this.elements['password'].value);
		this.reset();
	});
}

/**
 * Displays raw server response in 'raw' element.
 * @param (object) response - Response to display
 */
function displayRaw(response) {	// For debug
	document.getElementById('raw').textContent = (typeof response === 'string') ? response : JSON.stringify(response, null, 2);
}

/**
 * Creates and returns a new entry box.
 * 
 * @param {string} className class identifier
 * @param {string} title displayed title
 * @param {string} url url to box actions
 * @param {Object} object object defining box forms
 * @param {function(Object, string)} [action] action performed on click of entries spawned by this box
 * @returns
 */
function createEntryBox(className, title, url, object, action) {
	var box = document.importNode(
						document.getElementById('entryBox').content.querySelector('.entryBox'), true);	// Get live clone
	
	box.classList.add(className);
	box.addEventListener('click', function(event) {
		if (event.target !== this) return;
		
		var list = box.getElementsByClassName('entryList')[0];
		list.textContent = "Retrieving " + title + "...";

		rest.ajax('GET', url, null, response => {
			displayRaw(response);

			list.textContent = "";

			if (typeof response === 'object') {
				for (var key in response) {
					list.appendChild(createEntry(title + ": " + key, url + "/" + key, response[key], action));
				}
			}
		});
	});
	box.getElementsByClassName('title')[0].textContent = title;
	box.getElementsByClassName('direct')[0].href = rest.getUrl(url);

	var createForm = box.getElementsByClassName('createForm')[0];
	createForm.action = rest.getUrl(url);
	createForm.method = 'POST';

	var createFormFieldset = createForm.elements['root'];
	formify(createFormFieldset, object);

	createForm.elements['submitJson'].addEventListener('click', event => {
		rest.ajax('POST', url, JSON.stringify(objectify(createFormFieldset)), response => displayRaw(response));
		createForm.reset();
	});
	return box;
}
/**
 * Constructs and returns a new entry.
 * 
 * @param {string} name entry name
 * @param {string} url url to entry actions
 * @param {Object} object object to transform to entry
 * @param {function(Object, string)} [action] action performed on entry click
 * @returns
 */
function createEntry(name, url, object, action) {
	var entry = document.importNode(
							document.getElementById('entry').content.querySelector('.entry'), true);	// Get live clone
	entry.getElementsByClassName('title')[0].textContent = name;
	entry.getElementsByClassName('direct')[0].href = rest.getUrl(url);

	var updateForm = entry.getElementsByClassName('updateForm')[0];
	var updateFormFieldset = updateForm.elements['root'];
	formify(updateFormFieldset, object);

	updateForm.addEventListener('submit', event => {
		event.preventDefault();
		rest.ajax('PUT', url, JSON.stringify(objectify(updateFormFieldset)), response => displayRaw(response));
	});
	entry.getElementsByClassName('delete')[0].addEventListener('click', () => {
		rest.ajax('DELETE', url, null, response => displayRaw(response));
	});
	if (action) {
		entry.addEventListener('click', function(event) {
			if (event.target === this) action.apply(this, [object, url]);
		});
	}
	return entry;
}

/**
 * Transforms an object into a set of fields within a fieldset.
 * 
 * @param {Node} fieldset fieldset to apply object to
 * @param {Object} object object to apply
 */
function formify(fieldset, object) {
	for (var property in object) {
		if (typeof object[property] === 'object' && !(object[property] instanceof Date)) {	// TODO Ugly, hacky
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
						input.defaultChecked = "";	// Hack for checkboxes
						return 'checkbox';
					case 'object':
						if (value instanceof Date) return 'date';
						else return null;
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

/**
 * Retrieves and displays all users under a project in the 'usersList' element.
 * @param (string) projectId - ID of project owning users
 */
function showUsers(projectId) {
	document.getElementById('usersBox').style.display = "block";

	var usersList = document.getElementById('usersList');
	usersList.innerHTML = "Getting users for project: " + projectId + "...";

	var url = "projects/" + projectId + "/users";
	rest.ajax("GET", url, null, function(users) {
		displayRaw(users);

		document.getElementById('usersDirect').setAttribute('href', restRoot + url);
		usersList.innerHTML = "Project: " + projectId;

		for (var key in users) {
			(function(url, key) {
				var entry = usersList.appendChild(createEntry("User: " + key, users[key]));
				entry.appendChild(createButton("DELETE", function() {
					rest.ajax('DELETE', url + "/" + key, null, function(response) {
						displayRaw(response);
					});
				}));
			})(url, key);
		}
	});
}
