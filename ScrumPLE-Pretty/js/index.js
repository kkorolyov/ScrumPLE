window.addEventListener('load', init);

function init() {
	document.getElementById('search_bar').addEventListener('input', event => {
		var input = event.target.value;
		var _str = '{"name":"'+input+'"}';
		JSON.parse(_str);
		rest.ajax('POST', '/rest/projects', _str, response => displayRaw(response));
	});
}

function displayRaw(response) {
	var _str = (typeof response === 'string') ? response : JSON.stringify(response, null, 2);
	document.getElementById('projectList').innerHTML = _str;
}