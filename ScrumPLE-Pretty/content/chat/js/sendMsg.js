    function sendMsg(){
			socket.emit('chat message', $('#m').val());
			$('#m').val('');
      return false;
    }