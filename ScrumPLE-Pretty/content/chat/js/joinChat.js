function joinChat(){
			socket.emit('join', $('#n').val());
			$('#n').val('');
      document.getElementById('n').style.display='none';
      document.getElementById('joinbtn').style.display='none';
			return false;
    }