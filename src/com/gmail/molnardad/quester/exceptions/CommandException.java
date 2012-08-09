package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public final class CommandException extends QuesterException {

	private static final long serialVersionUID = 5178371486562967209L;

	private final String msg;
	
	public CommandException() {
		msg = "Invalid arguments.";
	}
	
	public CommandException(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String message() {
		return ChatColor.RED + msg;
	}

	@Override
	public String cause() {
		return "";
	}

}
