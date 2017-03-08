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

				switch (message.getCommand()) {
					case "connect":
						log.info("user <{}> connected", message.getUsername());
						this.username = message.getUsername();
                        Message connectMessage = new Message();
                        connectMessage.setCommand(message.getCommand());
                        connectMessage.setContents(message.getUsername() + " has connected"  );
                        for (ClientHandler handler : this.getServer().getHandlers()) {
                            handler.messageUser(connectMessage);
                        }
						break;
					case "disconnect":
						log.info("user <{}> disconnected", message.getUsername());
                        Set<ClientHandler> myHandlers = this.getServer().getHandlers();
                        Message disconnect = new Message();
                        disconnect.setCommand("disconnect");
                        disconnect.setContents(message.getUsername() + " has disconnected"  );
                        if (this.getServer().getHandlers().size() > 0) {
                            for (ClientHandler handler : myHandlers) {
                                handler.messageUser(disconnect);
                            }
                        }
                        this.getServer().getHandlers().remove(this);
						this.socket.close();
						break;
					case "echo":
						log.info("user <{}> echoed message <{}> timestamp <{}> addressee is <{}>" ,
                                message.getUsername(),
                                message.getContents(),
                                message.getTimeStamp(),
                                message.getAddressee());
						String response = mapper.writeValueAsString(message);
						writer.write(response);
						writer.flush();
						break;
					case "broadcast":
                        Set<ClientHandler> momoHandlers = this.getServer().getHandlers();
                        for (ClientHandler handler : momoHandlers) {
                            handler.messageUser(message);
                        }
						break;
					case "whisper":
						Set<ClientHandler> moreHandlers = this.getServer().getHandlers();
						for (ClientHandler handler : moreHandlers) {
						    if (handler.getUsername().equals(message.getAddressee())) {
						        Message newMessage = new Message(message.getUsername(),
                                        "whisper",
                                        "",
                                        message.getContents(),
                                        message.getTimeStamp());
                                handler.messageUser(newMessage);
                            }
                        }
						break;
					case "users":
                        Message allUsersList = new Message();
                        allUsersList.setTimeStamp(message.getTimeStamp());
                        StringBuilder builder = new StringBuilder();
                        for (ClientHandler handler : this.getServer().getHandlers()) {
                            builder.append("\n");
                            builder.append(handler.getUsername());
                        }
                        allUsersList.setContents(builder.toString());
                        allUsersList.setAddressee("");
                        allUsersList.setCommand("users");
                        this.messageUser(allUsersList);
                        log.info(allUsersList.getContents());
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
