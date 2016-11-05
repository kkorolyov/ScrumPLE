package org.scrumple.scrumplecore.chat;

import java.net.*;
import java.util.*;

import org.scrumple.scrumplecore.database.Database;

import dev.kkorolyov.simplelogs.Logger;
import dev.kkorolyov.simplelogs.Logger.Level;

import java.io.*;

public class chatClient {
	private Socket socket;
	private ObjectInputStream reader;
	private ObjectOutputStream writer;
	private static final Logger log = Logger.getLogger(Database.class.getName(), Level.DEBUG, new PrintWriter(System.err));

	public chatClient(){
		socket = new Socket();
		try {
			reader = new ObjectInputStream(socket.getInputStream());
			writer = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.exception(e);
		}
	}
}
