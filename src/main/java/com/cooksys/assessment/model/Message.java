package com.cooksys.assessment.model;

import com.sun.istack.internal.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

	private String username = "";
	private String command = "";
	private String contents = "";
	private String timeStamp = "";

	public Message() {
		// empty constructor for jackson
	}
	
	public Message(String username, String command, String contents, String timeStamp) {
		this.username = username;
		this.command = command;
		this.contents = contents;
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		Date date = new Date();
		this.timeStamp = df.format(date);
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsernamePsudo() {
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

	public String getCommandPsudo() {
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
	
	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
}
