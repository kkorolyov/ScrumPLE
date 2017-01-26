var display = document.getElementById("display");

function getProjects() {
	var xhttp = new XMLHttpRequest();
	display.innerHTML = xhttp;
	xhttp.onreadystatechange = function() {
		display.innerHTML = (this.readyState == 4) ? this.responseXML : this.readyState;
	}
	xhttp.open("GET", "http://localhost:8080/ScrumPLE-Core/rest/projects", true);
	xhttp.send();
}
function getUsers(project) {
	display.innerHTML = "Getting users for project: " + project;
}