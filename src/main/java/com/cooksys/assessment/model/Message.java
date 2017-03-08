package com.cooksys.assessment.model;

import com.sun.istack.internal.Nullable;

public class Message {

	private String username = "";
	private String command = "";
	private String addressee = "";
	private String contents = "";
	private String timeStamp = "";

	public Message() {
		// empty constructor for jackson
	}
	
	public Message(String username, String command, String addressee, String contents, String timeStamp) {
		this.username = username;
		this.command = command;
		this.addressee = addressee;
		this.contents = contents;
		this.timeStamp = timeStamp;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

    public String getAddressee() {
        return this.addressee;
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
}
