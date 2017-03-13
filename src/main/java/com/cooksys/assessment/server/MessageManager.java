package com.cooksys.assessment.server;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by student-2 on 3/13/2017.
 */
public class MessageManager {

    private Map<String, ClientHandler> handlers = Collections.synchronizedMap(new HashMap<>());

    public MessageManager() {}

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
                handler.messageUser(message);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }
}
