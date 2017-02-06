"use strict";

var restRoot = "http://192.168.1.195:8080/scrumple/rest/";
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

function ajaxReady(method, url, handler) {	// Main REST invocation
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState === 4) {
			var response = JSON.parse(this.responseText);
			
			displayRaw(response);
			handler(response);
		}
	}
	xhttp.open(method, restRoot + url, true);
	
	return xhttp;
}
function get(url, handler) {
	ajaxReady("GET", url, handler).send();
}
function post(url, content, handler) {
	var xhttp = ajaxReady("POST", url, handler);
	xhttp.setRequestHeader("Content-type", "application/json");
	xhttp.send(content);
}
function del(url, id, handler) {
	ajaxReady("DELETE", url + "/" + id, handler).send();
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
	
	get("projects", function(projects) {
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
	
	post("projects", JSON.stringify(project), function(response){});
}
function deleteProject(projectId) {
	del("projects", projectId, function(response){});
}

function showUsers(projectId, container) {
	
}
function createUser(projectId, handle, password) {
	
}


