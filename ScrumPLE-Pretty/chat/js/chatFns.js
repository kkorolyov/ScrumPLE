function sendMsg(){
    socket.emit('chat message', $('#m').val());
	$('#m').val('');
    return false;
}
function joinChat(){
	socket.emit('join', $('#n').val());
	$('#n').val('');
    document.getElementById('n').style.display='none';
    document.getElementById('joinbtn').style.display='none';
	return false;
}
var socket = io("https://52.10.231.227:3000");
document.getElementById("sendbtn").onclick=sendMsg;
document.getElementById("joinbtn").onclick=joinChat;
socket.on('chat message', function(msg){
	$('#messages').append($('<li>').text(msg));
});