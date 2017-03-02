function urlQuery(name, url) {
    if(!url){
        url = window.location.href;
    }
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if(!results[2]) return'';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function logIn(){
    // if auth is successful, show project details and hide login form
    document.getElementById('login').style.display='none';
    document.getElementById('project').style.display='block';
    var projectId = urlQuery('projectId');
    const url = "projects/" + projectId;
    rest.ajax('GET', url, null, response => {
        //var projectDetails = JSON.stringify(response,['name', 'description', 'visible']);
        document.getElementById('projectinfo').value = JSON.stringify(response, null, 2); 
    });
    const usersUrl = "projects/" + projectId + "/users";
    rest.ajax('GET',usersUrl, null, response => {
        textValue = document.getElementById('projectinfo').value;
        document.getElementById('projectinfo').value= textValue + JSON.stringify(response, null, 2);
    });
    return false;
}

document.getElementById("loginform").addEventListener('submit', function(event) {
    event.preventDefault();
    logIn();
    this.reset();
});
