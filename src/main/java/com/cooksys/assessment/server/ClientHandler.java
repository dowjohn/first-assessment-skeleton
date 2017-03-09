package com.cooksys.assessment.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.deploy.util.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientHandler implements Runnable {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);

	private Socket socket = null;
	private ObjectMapper mapper = null;
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	private String username = null;
	private Server server;

	public ClientHandler(Socket socket, Server server) {
		super();
		this.socket = socket;
		this.server = server;
	}

	public void run() {
		try {
			mapper = new ObjectMapper();
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

			while (!socket.isClosed()) {
				String raw = reader.readLine();
				Message message = mapper.readValue(raw, Message.class);
				message.setTimestamp();
				Set<ClientHandler> clientHandlers = getServer().getHandlers();

				switch (message.getCommandPseudo()) {
					case "connect":
						log.info("connect");
						this.setUsername(message.getUsername());
						if (userExistCheck(this.getServer().getHandlers(), this.getUsername())) {
						    message.setCommand("usertaken");
						    messageUser(message);
                            clientHandlers.remove(this);
						    socket.close();
						    break;
                        } else {
                            messageUsersBatch(message, clientHandlers);
                            break;
                        }
					case "disconnect":
					    log.info("disconnect");
                        messageUsersBatch(message, clientHandlers);
                        clientHandlers.remove(this);
						socket.close();
						break;
					case "echo":
					    log.info("echo");
						messageUser(message);
						break;
					case "broadcast":
					    log.info("broadcast");
                        messageUsersBatch(message, clientHandlers);
						break;
					case "@":
					    log.info("whisper");
						boolean userExists = false;
                        ClientHandler addressee = null;
						for (ClientHandler handler : clientHandlers) {
						    if (handler.getUsername().equals(message.getUsernamePseudo())) {
						        addressee = handler;
						        userExists = true;
                            }
                        }
                        if (userExists) {
                            addressee.messageUser(message);
                        } else {
						    Message doesNotExist = new Message(message.getCommand().substring(1), "userdoesnotexist", "user does not exist");
						    this.messageUser(doesNotExist);
                        }
						break;
					case "users":
					    log.info("users");
                        StringBuilder builder = new StringBuilder();
                        for (ClientHandler handler : clientHandlers) {
                            builder.append("\n");
                            builder.append(handler.getUsername());
                        }
                        message.setContents(builder.toString());
                        this.messageUser(message);
						break;
				}
			}
		} catch (IOException e) {
			log.error("Something went wrong :/", e);
		}
	}

	private void messageUser(Message message) throws JsonProcessingException {
        String response = mapper.writeValueAsString(message);
        writer.write(response);
        writer.flush();
    }

    private void messageUsersBatch(Message message, Set<ClientHandler> clientHandlers) throws JsonProcessingException {
        for (ClientHandler handler : clientHandlers) {
            handler.messageUser(message);
        }
    }

    public String getUsername() {
	    return this.username;
    }

    public void setUsername(String username) {
	    this.username = username;
    }

    public Server getServer() {
	    return this.server;
    }

    public void setServer(Server server) {
	    this.server = server;
    }

    public boolean userExistCheck(Set<ClientHandler> set, String usersName) {
	    boolean doesExist = false;
	    int quasiCounter = 0;
	    for (ClientHandler client : set) {
	        if (client.getUsername().equals(usersName)) {
	            quasiCounter ++;
            }
        }
        if (quasiCounter > 1) {
	        doesExist = true;
        }
	    return doesExist;
    }
}
