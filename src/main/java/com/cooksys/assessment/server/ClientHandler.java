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

				switch (message.getCommandPsudo()) {
					case "connect":
						log.info("user <{}> connected", message.getUsername());
						this.username = message.getUsername();
                        Message connectMessage = new Message();
                        connectMessage.setCommand(message.getCommand());
                        connectMessage.setUsername(message.getUsername());
                        connectMessage.setTimeStamp();
                        for (ClientHandler handler : this.getServer().getHandlers()) {
                            handler.messageUser(connectMessage);
                        }
						break;
					case "disconnect":
                        Set<ClientHandler> myHandlers = this.getServer().getHandlers();
                        if (this.getServer().getHandlers().size() > 0) {
                            for (ClientHandler handler : myHandlers) {
                                handler.messageUser(message);
                            }
                        }
                        this.getServer().getHandlers().remove(this);
						this.socket.close();
						break;
					case "echo":
						messageUser(message);
						break;
					case "broadcast":
                        Set<ClientHandler> momoHandlers = this.getServer().getHandlers();
                        for (ClientHandler handler : momoHandlers) {
                            handler.messageUser(message);
                        }
						break;
					case "@":
						Set<ClientHandler> moreHandlers = this.getServer().getHandlers();
						for (ClientHandler handler : moreHandlers) {
						    if (handler.getUsername().equals(message.getUsernamePsudo())) {
                                handler.messageUser(message);
                            }
                        }
						break;
					case "users":
                        Message allUsersList = new Message();
                        StringBuilder builder = new StringBuilder();
                        for (ClientHandler handler : this.getServer().getHandlers()) {
                            builder.append("\n");
                            builder.append(handler.getUsername());
                        }
                        allUsersList.setContents(builder.toString());
                        allUsersList.setCommand("users");
                        this.messageUser(allUsersList);
						break;
				}
			}

		} catch (IOException e) {
			log.error("Something went wrong :/", e);
		}
	}

	private void messageUser(Message message) throws JsonProcessingException {
	    log.info("Got to messageUser");
	    message.setTimeStamp();
        String response = mapper.writeValueAsString(message);
        writer.write(response);
        writer.flush();
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
}
