package com.cooksys.assessment.model;

import com.sun.istack.internal.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

	private String username = "";
	private String command = "";
	private String contents = "";
	private String timestamp = "";

	public Message() {
		// empty constructor for jackson
	}
	
	public Message(String username, String command, String contents) {
		this.username = username;
		this.command = command;
		this.contents = contents;
		setTimestamp();
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsernamePseudo() {
		if (this.command.startsWith("@")) {
			return command.substring(1);
		} else {
			return getUsername();
		}
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCommandPseudo() {
		if (this.command.startsWith("@")) {
			return "@";
		} else {
			return this.command;
		}
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
	
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp() {
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		Date date = new Date();
		this.timestamp = df.format(date).toString();
		System.out.println(getTimestamp());
	}
}
