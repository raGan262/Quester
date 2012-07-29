package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public final class QuestExistenceException extends QuesterException {

	private static final long serialVersionUID = -2808653441439451874L;

	private final boolean exists;
	private final String cause;
	
	public QuestExistenceException(String cause, boolean exists) {
		this.exists = exists;
		this.cause = cause;
	}

	@Override
	public String message() {
		if(exists) {
			return ChatColor.RED + "Quest already exists.";
		} else {
			return ChatColor.RED + "Quest does not exist.";
		}
	}
	
	@Override
	public String cause() {
		return cause;
	}
}
