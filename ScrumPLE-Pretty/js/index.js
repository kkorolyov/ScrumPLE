"use strict"

window.addEventListener('load', init);

function init() {
	document.getElementById('search_bar').addEventListener('input', event => {
		let input = event.target.value;
		if (!input) {
			return;
		}
		document.getElementById('search_bar').innerHTML = ""
		//var _str = '{"name":"'+input+'"}';
		//JSON.parse(_str);
		rest.ajax('GET','projects?name='+input, null, response => {
			if (typeof response === 'object') {
				for (let key in response ) {
					let url = "/project/project.html?projectId=" + key
					let name = response[key].name
					let project-list = document.getElementById("project-list")
					let link = project-list.appendChild(document.createElement('a'))
					link.href = url
					link.textContent = name
					project-list.appendChild(document.createElement ('br'))
				}
			}
		})
	})
}
