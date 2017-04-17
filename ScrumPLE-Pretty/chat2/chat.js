var express = require('express');
var app = require('express')();
var http = require('http').createServer(app);
var io = require('socket.io')(http);

app.use(express.static(__dirname + '/public'));

app.get('/', function (req, res) {
    //res.sendfile('index.html');
});

var connectedSockets={};
var allUsers=[];
io.on('connection',function(socket){
    socket.on('addUser',function(data){ 
        socket.emit('userAddingResult',{result:true});
        socket.displayName=data.displayName;
        connectedSockets[socket.displayName]=socket;
        allUsers.push(data);
        socket.broadcast.emit('userAdded',data);
        socket.emit('allUser',allUsers);
    });

    socket.on('addMessage',function(data){ 
        if(data.to){
            connectedSockets[data.to].emit('messageAdded',data);
        }
    });

    socket.on('getAllUsers',function(data){
        console.log('getAllUsers');
        var allUsersExceptMe=[];
        for (var i = 0; i < allUsers.length; i++) {
            if (allUsers[i].displayName != data.displayName) {
                allUsersExceptMe.push(allUsers[i]);
            }
        }
        console.log(allUsersExceptMe);
        socket.emit('allUser',allUsersExceptMe);
    });

    socket.on('disconnect', function () {  
            console.log(new Date().toLocaleString()+'out');
            socket.broadcast.emit('userRemoved', { 
                displayName: socket.displayName
            });
            for(var i=0;i<allUsers.length;i++){
                if(allUsers[i].displayName==socket.displayName){
                    allUsers.splice(i,1);
                }
            }
            delete connectedSockets[socket.displayName];

        }
    );
}); 

http.listen(3002, function () {
    console.log('listening on *:3002');
});