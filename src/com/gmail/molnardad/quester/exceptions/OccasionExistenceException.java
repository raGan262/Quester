package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public final class OccasionExistenceException extends QuesterException {

	private static final long serialVersionUID = 5137371329562967209L;

	
	public OccasionExistenceException() {
	}
	
	@Override
	public String message() {
		return ChatColor.RED + "No such occasion.";
	}

	@Override
	public String cause() {
		return "";
	}

}
