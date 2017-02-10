var app = require('express')();
var http = require('http').Server(app);
//var io = require('socket.io')(https);
var fs = require('fs');
var https = require('https');
var privateKey = fs.readFileSync('/etc/ssl/private/node-self.key');
var certificate = fs.readFileSync('/etc/ssl/certs/node-self.crt');
var credentials = {key: privateKey, cert: certificate};

//var httpServer = http.createServer(app);
var httpsServer = https.createServer(credentials, app);
var io = require('socket.io')(httpsServer);

app.get('/', function(req, res){
    res.sendFile('/opt/git/scrumple/ScrumPLE-Pretty/content/chat/chat.html');
});

io.on('connection', function(socket){
	console.log('user connected');
	socket.on('chat message', function(msg){
		console.log('message: ' + msg);
		io.emit('chat message', msg);
	});
	socket.on('disconnect', function(){
		console.log('user disconnected');
	});
});

httpsServer.listen(3000, function(){
    console.log('listening on *:3000');
});
