package org.scrumple.scrumplecore.chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;

public class ChatClient {
	private Socket socket;
	private ObjectInputStream reader;
	private ObjectOutputStream writer;
	private String serverAddress, username;
	private int port;
	private static final Logger log = Logger.getLogger(ChatClient.class.getName(), Level.DEBUG, new PrintWriter(System.err));

	public ChatClient(String address, int port, String username){
		
		this.serverAddress = address;
		this.port = port;
		this.username = username;
		socket = new Socket();
		
	}
	
	public boolean startClient() {
		
		
		try {
			socket = new Socket(serverAddress, port);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			log.exception(e);
			return false;
		}
		
		try {
			reader = new ObjectInputStream(socket.getInputStream());
			writer = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.exception(e);
			return false;
		}
		
		try {
			writer.writeObject(username);
		} catch (IOException e) {
			System.out.println("Exception during login by : " + username);
			log.exception(e);
			return false;
		}
		return true;
	}
	
	void sendMessage(String msg) {
		try {
			writer.writeObject(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.exception(e);
		}
	}
}
