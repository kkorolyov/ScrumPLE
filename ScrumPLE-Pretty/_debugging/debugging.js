"use strict";

window.addEventListener('load', init);

function init() {
	const body = document.getElementsByTagName('body')[0];
	body.appendChild(createProjectsBox());
	body.appendChild(document.createElement('div')).id = 'selectionBox';

	applyEventListeners();
}

const select = (function () {
	const selected = 'selected';
	const selects = [];

	return function (node) {
		selects.forEach(e => e.classList.remove(selected));

		node.classList.add(selected);
		selects.push(node);
	}
})();

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
	function (object, url) {
		select(this);

		const box = document.getElementById('selectionBox');
		while(box.firstChild) box.removeChild(box.firstChild);

		box.appendChild(createUsersBox(object.name, url));
		box.appendChild(createMeetingsBox(object.name, url));
	});
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
			this.disabled = true;

			const oldCredentials = rest.credentials;
			rest.login("d@bugg3r", "d3bug1t!");

			rest.ajax("GET", "debug/reset", null, response => {
				displayRaw(response);

				this.disabled = false;
			});
			rest.credentials = oldCredentials;
		}
	});
	document.getElementById('loginForm').addEventListener('submit', function(event) {
		event.preventDefault();

		let project = document.getElementsByClassName('selected')[0].textContent

		rest.login(this.elements['handle'].value, this.elements['password'].value, project);
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
 * @param {string} name box name
 * @param {string} url url to box actions
 * @param {Object} object object defining box forms
 * @param {function(Object, string)} [action] action performed on click of entries spawned by this box
 * @returns
 */
function createEntryBox(className, name, url, object, action) {
	const box = document.importNode(
						document.getElementById('entryBox').content.querySelector('.entryBox'), true);	// Get live clone
	const title = box.getElementsByClassName('title')[0];
	const direct = box.getElementsByClassName('direct')[0];
	const createForm = box.getElementsByClassName('createForm')[0];
	const createFormFieldset = createForm.elements['root'];

	box.classList.add(className);

	title.textContent = name;
	title.classList.add('clickable');
	title.addEventListener('click', function(event) {
		if (event.target !== this) return;
		
		const list = box.getElementsByClassName('entryList')[0];
		list.textContent = "Retrieving " + name + "...";

		rest.ajax('GET', url, null, response => {
			displayRaw(response);

			list.textContent = "";

			if (typeof response === 'object') {
				for (let key in response) {
					list.appendChild(createEntry(key, url + "/" + key, response[key], action));
				}
			}
		});
	});
	direct.href = rest.getUrl(url);

	formify(createFormFieldset, object);

	createForm.addEventListener('submit', event => {
		event.preventDefault();

		rest.ajax('POST', url, JSON.stringify(objectify(createFormFieldset)), response => displayRaw(response));
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
	const entry = document.importNode(
							document.getElementById('entry').content.querySelector('.entry'), true);	// Get live clone
	const title = entry.getElementsByClassName('title')[0]
	const direct = entry.getElementsByClassName('direct')[0];
	const updateForm = entry.getElementsByClassName('updateForm')[0];
	const updateFormFieldset = updateForm.elements['root'];

	title.textContent = name;
	direct.href = rest.getUrl(url);

	formify(updateFormFieldset, object);

	updateForm.addEventListener('submit', event => {
		event.preventDefault();

		rest.ajax('PUT', url, JSON.stringify(objectify(updateFormFieldset)), response => displayRaw(response));
	});
	entry.getElementsByClassName('delete')[0].addEventListener('click', () => {
		rest.ajax('DELETE', url, null, response => displayRaw(response));
	});
	if (action) {
		title.classList.add('clickable');
		title.addEventListener('click', function(event) {
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
	for (let property in object) {
		if (typeof object[property] === 'object' && !(object[property] instanceof Date)) {	// TODO Ugly, hacky
			const nextForm = fieldset.appendChild(document.createElement('fieldset'));
			nextForm.name = property;	// ID new form by property name
			nextForm.appendChild(document.createElement('legend')).textContent = property;

			formify(nextForm, object[property]);	// Apply inner object's properties to new form
		} else {
			fieldset.appendChild(document.createTextNode(property + ": "));

			const input = fieldset.appendChild(document.createElement('input'));
			input.name = property;
			input.defaultValue = object[property];
			input.type = (value => {
				switch (typeof value) {
					case 'string': return 'text';
					case 'boolean': 
						if (value) input.defaultChecked = " ";	// Hack for checkboxes
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
	const object = {};

	for (let i = 0; i < fieldset.childNodes.length; i++) {
		const childNode = fieldset.childNodes[i];
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

	const usersList = document.getElementById('usersList');
	usersList.innerHTML = "Getting users for project: " + projectId + "...";

	const url = "projects/" + projectId + "/users";
	rest.ajax("GET", url, null, function(users) {
		displayRaw(users);

		document.getElementById('usersDirect').setAttribute('href', restRoot + url);
		usersList.innerHTML = "Project: " + projectId;

		for (let key in users) {
			(function(url, key) {
				const entry = usersList.appendChild(createEntry("User: " + key, users[key]));
				entry.appendChild(createButton("DELETE", function() {
					rest.ajax('DELETE', url + "/" + key, null, function(response) {
						displayRaw(response);
					});
				}));
			})(url, key);
		}
	});
}
