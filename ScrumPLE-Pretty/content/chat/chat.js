var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

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

http.listen(3000, function(){
    console.log('listening on *:3000');
});