var express = require('express');
var app = require('express')();
var fs = require('fs');
var http = require('http').createServer(app);
//var privateKey = fs.readFileSync('/etc/ssl/private/node-self.key');
//var certificate = fs.readFileSync('/etc/ssl/certs/node-self.crt');
//var credentials = {key: privateKey, cert: certificate};

//var httpsServer = https.createServer(credentials, app);
var io = require('socket.io')(http);

app.use(express.static(__dirname + '/public'));

var connectedSockets={};
var allUsers=[];
io.on('connection',function(socket){
    socket.on('addUser',function(data){
        let displayName=data.displayName;
        let projectName=data.projectName;
        console.log(projectName);
        if(connectedSockets[displayName]){
            return false;
        }else{
            socket.displayName=displayName;
            connectedSockets[displayName]=socket;
            if(!allUsers[projectName]){
                allUsers[projectName]=[{displayName:""}];
            }
            allUsers[projectName].push({displayName:displayName});
            socket.broadcast.emit('userAdded',{displayName:displayName,projectName:projectName});
        }
    });

    socket.on('addMessage',function(data){
        if(data.to){
            connectedSockets[data.to].emit('messageAdded',data);
        }else{
            socket.broadcast.emit('messageAdded',data);
        }
    });

    socket.on('getAllUsers',function(data){
        let allUsersExceptMe=[];
        let projectName=data.projectName;
        let displayName=data.displayName;
        if(!allUsers[projectName]){
            allUsers[projectName] =[];
        }
        var projectUsers=allUsers[projectName];
        for (var i = 0; i < projectUsers.length; i++) {
            if (projectUsers[i].displayName != displayName) {
                allUsersExceptMe.push(projectUsers[i]);
            }
        }
        console.log("getAllUsers:"+allUsersExceptMe);
        socket.emit('allUser',allUsersExceptMe);
    });

    socket.on('disconnect', function () {
            console.log(new Date().toLocaleString()+'out');
            let projectName= socket.projectName;
            let displayName=socket.displayName;

            socket.broadcast.emit('userRemoved', {
                displayName: displayName
                ,projectName:projectName
            });
            if(!allUsers[projectName]){
                allUsers[projectName]=[];
            }
            let projectUsers=allUsers[projectName];
            for(var i=0;i<projectUsers.length;i++){
                if(projectUsers[i].displayName==displayName){
                    projectUsers.splice(i,1);
                }
            }
            delete connectedSockets[displayName];

        }
    );
});

http.listen(3000, function () {
    console.log('listening on *:3000');
});