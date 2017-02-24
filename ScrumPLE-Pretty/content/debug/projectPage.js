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

function showProject(){
    var projectId = urlQuery('projectId');
    const url = "projects/" + projectId;
    rest.ajax('GET', url, null, response => {
        document.getElementById('project').innerHTML=response;
    });
}
function logIn(){
    // if auth is successful, show project details and hide login form
    document.getElementById('login').style.display='none';
    document.getElementById('project').style.display='block';
    return false;
}

document.getElementById("log").onclick=logIn;
document.getElementById("log").onclick=showProject;