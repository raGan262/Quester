package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public final class ObjectiveCompletionException extends QuesterException {

	private static final long serialVersionUID = 5137371329562967209L;

	
	public ObjectiveCompletionException() {
	}
	
	@Override
	public String message() {
		return ChatColor.RED + "Not enough resources to complete current objective.";
	}

	@Override
	public String cause() {
		return "";
	}

}
