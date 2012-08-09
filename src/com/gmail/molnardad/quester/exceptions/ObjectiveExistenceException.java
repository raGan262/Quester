package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public final class ObjectiveExistenceException extends QuesterException {

	private static final long serialVersionUID = 5137371486562967209L;

	
	public ObjectiveExistenceException() {
	}
	
	@Override
	public String message() {
		return ChatColor.RED + "Objective does not exist.";
	}

	@Override
	public String cause() {
		return "";
	}

}
