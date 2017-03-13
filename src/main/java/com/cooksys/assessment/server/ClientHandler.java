package com.cooksys.assessment.server;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);

	private Socket socket = null;
	private ObjectMapper mapper = null;
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	private MessageManager manager;

	public ClientHandler(Socket socket, MessageManager manager) {
		super();
		this.socket = socket;
		this.manager = manager;
	}

	public void run() {
		try {
			mapper = new ObjectMapper();
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

			while (!socket.isClosed()) {
				String raw = reader.readLine();
				Message message = mapper.readValue(raw, Message.class);
				message.setTimestamp(Message.generateTimestamp());

				switch (message.getCommandPseudo()) {
					case "connect":
						log.info("connect");
						if (manager.getHandlers().containsKey(message.getUsername())) {
						    message.setCommand("usertaken");
						    messageUser(message);
						    socket.close();
						    break;
                        } else {
						    manager.addStringClientHandler(message.getUsername(), this);
                            manager.messageAllUsers(manager.getHandlers(), message);
                            break;
                        }
					case "disconnect":
					    log.info("disconnect");
                        manager.messageAllUsers(manager.getHandlers(), message);
                        manager.getHandlers().remove(message.getUsername());
						socket.close();
						break;
					case "echo":
					    log.info("echo");
						messageUser(message);
						break;
					case "broadcast":
					    log.info("broadcast");
                        manager.messageAllUsers(manager.getHandlers(), message);
						break;
					case "@":
					    log.info("whisper");
                        if (manager.getHandlers().containsKey(message.getUsernamePseudo())) {
                            log.info("User DOES exist!");
                            manager.getHandlers().get(message.getUsernamePseudo()).messageUser(message);
						} else {
                            Message doesNotExist = new Message(message.getCommand().substring(1), "userdoesnotexist", "user does not exist");
                            doesNotExist.setTimestamp(Message.generateTimestamp());
                            this.messageUser(doesNotExist);
                        }


						break;
					case "users":
					    log.info("users");
                        StringBuilder builder = new StringBuilder();
                        for (String username : manager.getHandlers().keySet()) {
                            builder.append("\n");
                            builder.append(username);
                        }
                        message.setContents(builder.toString());
                        messageUser(message);
						break;
				}
			}
		} catch (IOException e) {
			log.error("Something went wrong :/", e);
		}
	}

	public void messageUser(Message message) throws JsonProcessingException {
        String response = mapper.writeValueAsString(message);
        writer.write(response);
        writer.flush();
    }

    public MessageManager getMessageManager() {
	    return this.manager;
    }

    public void setMessageManager(MessageManager manager) {
	    this.manager = manager;
    }
}
