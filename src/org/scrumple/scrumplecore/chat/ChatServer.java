package org.scrumple.scrumplecore.chat;

import java.io.*;
import java.net.*;
import java.util.*;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;

public class ChatServer {
	private ArrayList<ClientThread> clients;
	private int port;
	private boolean on;
	private static final Logger log = Logger.getLogger(ChatServer.class.getName(), Level.DEBUG, (PrintWriter[]) null);
	public ChatServer(int port) {
		this.port = port;
		clients = new ArrayList<ClientThread>();
	}
	
	public void start() {
		on = true;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			
			while(on){
				System.out.println("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();
				
				if(!on)
					break;
				ClientThread t = new ClientThread(socket);
				clients.add(t);
				t.start();
			}
			
			serverSocket.close();
			for(int i = 0; i < clients.size(); ++i) {
				ClientThread tc = clients.get(i);
				tc.reader.close();
				tc.writer.close();
				tc.socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.exception(e);
		}
	}
	
	protected void stop() {
		on = false;
	}
	
	class ClientThread extends Thread{
		
		Socket socket;
		ObjectInputStream reader;
		ObjectOutputStream writer;
		
		String userName, msg;

		public ClientThread(Socket aSocket) {
			this.socket = aSocket;
			
			try {
				reader = new ObjectInputStream(socket.getInputStream());
				writer = new ObjectOutputStream(socket.getOutputStream());
				
				userName = (String) reader.readObject();
				System.out.println(userName + " has entered.");
			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				log.exception(e);
			}
		}
		//TODO
	}
}
