"use strict";

var restRoot = "http://localhost:8080/ScrumPLE/rest/";

function showProjects(el) {
	el.innerHTML = "Getting Projects...";
	
	ajax("GET", "projects", function(projects) {
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

function showProjectMenu(project) {
	project.appendChild(document.createTextNode("Test"));
}

function ajax(method, url, handler) {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState === 4) {
			var response = JSON.parse(this.responseText);
			
			displayRaw(response);
			handler(response);
		}
	}
	xhttp.open(method, restRoot + url, true);
	xhttp.send();
}

function displayRaw(response) {
	document.getElementById('raw').innerHTML = JSON.stringify(response, null, 2);
}
