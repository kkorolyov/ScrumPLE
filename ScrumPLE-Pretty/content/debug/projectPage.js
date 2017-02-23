function logIn(){
    // if auth is successful, show project details and hide login form
    document.getElementById('login').style.display='none';
    document.getElementById('project').style.display='block';
    return false;
}

document.getElementById("log").onclick=logIn;