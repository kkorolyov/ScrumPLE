"use strict";

var restRoot = "http://localhost:8080/scrumple/rest/";

function showProjects(el) {
	el.innerHTML = "Getting Projects...";
	
	get("projects", function(projects) {
		el.innerHTML = "";
		
		for (var key in projects) {
			var entry = document.createElement('div');
			entry.setAttribute('class', "entry round");
			entry.setAttribute('title', "Click to expand");
			entry.setAttribute('onclick', "showProjectMenu(this); event.cancelBubble = true;");
			
			var name = document.createElement('h4');
			name.appendChild(document.createTextNode("Project: " + key));
			
			var attributes = document.createElement('p');
			for (var value in projects[key]) {
				attributes.appendChild(document.createTextNode(value + "=" + projects[key][value]));
				attributes.appendChild(document.createElement('br'));
			}
			entry.appendChild(name);
			entry.appendChild(attributes);
			
			el.appendChild(entry);
		}
	});
}

function createProject(name, description, isPrivate) {
	var project = {"name": name,
									"description": description,
									"isPrivate": isPrivate};
	
	post("projects", JSON.stringify(project), function(response){});
}

function showProjectMenu(project) {
	project.appendChild(document.createTextNode("Test"));
}

function ajaxReady(method, url, handler) {
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

function displayRaw(response) {
	document.getElementById('raw').innerHTML = JSON.stringify(response, null, 2);
}
