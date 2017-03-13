package com.cooksys.assessment.server;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class Server implements Runnable {
	private Logger log = LoggerFactory.getLogger(Server.class);
	
	private int port;
	private ExecutorService executor;
	private Map<String, ClientHandler> handlers = Collections.synchronizedMap(new HashMap());

	public Server(int port, ExecutorService executor) {
		super();
		this.port = port;
		this.executor = executor;
	}

	public void run() {
		log.info("server started");
		ServerSocket ss;
		try {
			ss = new ServerSocket(this.port);
			while (true) {
				Socket socket = ss.accept();
				ClientHandler handler = new ClientHandler(socket, this);
				executor.execute(handler);
            }
		} catch (IOException e) {
			log.error("Something went wrong in Server.class", e);
		}
	}

	public synchronized Map<String, ClientHandler> getHandlers() {
	    return this.handlers;
    }

    public synchronized void setHandlers(HashMap<String, ClientHandler> handlers) {
		this.handlers = handlers;
	}

	public synchronized void addStringClientHandler(String name, ClientHandler handler) {
	    getHandlers().put(name, handler);
    }

    public synchronized void messageAllUsers(Map<String, ClientHandler> handlers, Message message) {
        handlers.forEach((user, handler)->{
            try {
                log.info("messageAllUsers");
                log.info(message.getCommand());
                handler.messageUser(message);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }
}
