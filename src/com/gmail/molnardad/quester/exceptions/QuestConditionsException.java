package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public final class QuestConditionsException extends QuesterException {

	private static final long serialVersionUID = -4912562827310211604L;
	private final String cause;
	
	public QuestConditionsException(String cause) {
		this.cause = cause;
	}
	
	@Override
	public String message() {
		return ChatColor.RED + "One or more conditions are not met.";
	}

	@Override
	public String cause() {
		return cause;
	}

}
