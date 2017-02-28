"use strict";

window.addEventListener('load', init);

function init() {
	const body = document.getElementsByTagName('body')[0];
	//body.appendChild(createProjectsBox());
	createProjectsBox();
}

function createProjectsBox() {
	createEntryBox('projects');
}

/**
 * Displays raw server response in 'raw' element.
 * @param (object) response - Response to display
 */
function displayRaw(response) {	// For debug
	//document.getElementById('raw').textContent = (typeof response === 'string') ? response : JSON.stringify(response, null, 2);
	var _str = (typeof response === 'string') ? response : JSON.stringify(response, null, 2);
	alert(_str);
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
function createEntryBox(url) {
	//const box = document.importNode(
	//					document.getElementById('entryBox').content.querySelector('.entryBox'), true);	// Get live clone
	//const title = box.getElementsByClassName('title')[0];
	//const direct = box.getElementsByClassName('direct')[0];
	//const createForm = box.getElementsByClassName('createForm')[0];
	const createForm = document.getElementById('createForm');
	const createFormFieldset = createForm.getElementsByClassName('mycontent')[0];
	//const createFormFieldset = createForm.elements['root'];

	//box.classList.add(className);

	//formify(createFormFieldset, object);

	createForm.addEventListener('submit', event => {
		event.preventDefault();
		//alert(JSON.stringify(objectify(createFormFieldset)));
		//console.log(JSON.stringify(objectify(createFormFieldset)));
		var projectName = createForm.name.value;
		var projectDescription= createForm.description.value;
		var projectVisible = createForm.visible.checked;
		var projectHandle = createForm.handle.value;
		var projectPassword = createForm.password.value;
		var projectDisplayName = createForm.displayName.value;
		var projectRole = createForm.role.value;
		var _str = '{"name":"'+projectName+'","description":"'+projectDescription+'","visible":'+projectVisible+',"owner":{"credentials":{"handle":"'+projectHandle+'","password":"'+projectPassword+'"},"displayName":"'+projectDisplayName+'","role":"'+projectRole+'"}}';

		//alert(_str);

		// {"name":"11","description":"22","visible":true,"owner":{"credentials":{"handle":"33","password":"44"},"displayName":"55","role":"66"}}

		rest.ajax('POST', url, _str, response => displayRaw(response));
	});
	//return box;
}


/**
 * Transforms a fieldset into an object.
 * 
 * @param {Node} fieldset fieldset with fieldset or input elements to transform to object
 * @returns
 */
function objectify(fieldset) {
	const object = {};

	var inputs = fieldset.getElementsByClassName('need_input');
	for (let i = 0; i < inputs.length; i++) {
		switch (inputs[i].type) {
			case 'text':
			case 'password':
			case 'textarea':
				object[inputs[i].name] = inputs[i].value;
				break;
			case 'checkbox':
				object[inputs[i].name] = inputs[i].checked;
				break;
		}
	}

	
	/*for (let i = 0; i < fieldset.childNodes.length; i++) {
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
	*/
	return object;
}
