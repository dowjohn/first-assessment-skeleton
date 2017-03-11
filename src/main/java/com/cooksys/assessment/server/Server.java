package com.cooksys.assessment.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class Server implements Runnable {
	private Logger log = LoggerFactory.getLogger(Server.class);
	
	private int port;
	private ExecutorService executor;
	private Set<ClientHandler> handlers = Collections.synchronizedSet(new HashSet<>());

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
                handlers.add(handler);
            }
		} catch (IOException e) {
			log.error("Something went wrong in Server.class", e);
		}
	}

	public synchronized Set<ClientHandler> getHandlers() {
	    return this.handlers;
    }

    public void setHandlers(Set<ClientHandler> handlers) {
		this.handlers = handlers;
	}
}
