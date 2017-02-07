"use strict";

var restRoot = "https://192.168.1.195:8080/scrumple/rest/";
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
	
	if (content != null) {
		xhttp.setRequestHeader("Content-type", "application/json");
	}
	xhttp.send(content);
}

function displayRaw(response) {	// For debug
	document.getElementById('raw').innerHTML = JSON.stringify(response, null, 2);
}

function createEntry() { // Appends new, empty entry to container, returns entry
	var entry = document.createElement('div');
	entry.setAttribute('class', "entry round");
	entry.setAttribute('title', "Click to expand");
	
	return entry;
}

function showProjects(container) {	// Creates project boxes inside 'container'
	container.innerHTML = "Getting Projects...";
	
	ajax("GET", "projects", null, function(projects) {
		displayRaw(projects);
		
		container.innerHTML = "";
		
		for (var key in projects) {
			var entry = container.appendChild(createEntry(container));
			entry.appendChild(document.createElement('h4')).appendChild(document.createTextNode("Project: " + key));
			
			var attributes = entry.appendChild(document.createElement('p'));
			for (var value in projects[key]) {
				attributes.appendChild(document.createTextNode(value + "=" + projects[key][value]));
				attributes.appendChild(document.createElement('br'));
			}
			var button = entry.appendChild(document.createElement('button'));
			button.setAttribute('type', 'button')
			button.setAttribute('onclick', "deleteProject('" + key + "')");
			button.appendChild(document.createTextNode("DELETE"));
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
	ajax("DELETE", "projects", projectId, function(response) {
		displayRaw(response);
	});
}

function showUsers(projectId, container) {
	
}
function createUser(projectId, handle, password) {
	
}
function deleteUser(projectId, userId) {
	
}
