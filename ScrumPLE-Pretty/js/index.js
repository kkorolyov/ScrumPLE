window.addEventListener('load', init);

function init() {
	document.getElementById('search_bar').addEventListener('input', event => {
		var input = event.target.value;
		if (!input) {
			return;
		}
		//var _str = '{"name":"'+input+'"}';
		//JSON.parse(_str);
		rest.ajax('GET', 'projects?name='+input, null, response => displayRaw(response));
	});
}

function displayRaw(response) {
	var _str = (typeof response === 'string') ? response : JSON.stringify(response, null, 2);
	var innerHTML = '';
	if (typeof response == 'object') {
		innerHTML = '<ul>';
		for (var i in response) {
			if (response[i].visible)
				innerHTML += '<li style="cursor:pointer" onclick="window.open(\'view/login.html\',\'\',\'top=100,left=100,width=300,height=200\')">' + response[i].name + '</li>';
		}
		innerHTML += '</ul>';
	}
	document.getElementById('projectList').innerHTML = innerHTML;
}