var app = require('express')();
var http = require('http').Server(app);
var fs = require('fs');
var https = require('https');
var privateKey = fs.readFileSync('/etc/ssl/private/node-self.key');
var certificate = fs.readFileSync('/etc/ssl/certs/node-self.crt');
var credentials = {key: privateKey, cert: certificate};

var httpsServer = https.createServer(credentials, app);
var io = require('socket.io')(httpsServer);
var msgLog = [];
var people = {};

app.get('/', function(req, res){
    res.sendFile('/opt/git/scrumple/ScrumPLE-Pretty/content/chat/chat.html');
});

io.on('connection', function(socket){
	console.log('user connected');
	socket.on('join', function(name){
		people[socket.userID] = name;
		if(name != ''){
			io.emit("chat message", name + ' has joined the lobby.');
			msgLog.forEach(function(msg){
				socket.emit(msg);
			}); 
		}
	
		socket.on('chat message', function(msg){
			console.log('message: ' + msg);
			io.emit('chat message', name + ': ' + msg);
			msgLog.push(msg);
			msgLog.forEach(function(msg) {
				console.log(msg);
			});
		});
		socket.on('disconnect', function(){
			console.log('user disconnected');
			io.emit('chat message', name + ' has disconnected');
		});
	});
});

httpsServer.listen(3000, function(){
    console.log('listening on *:3000');
});
