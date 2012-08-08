package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public final class QuestWorldException extends QuesterException {

	private static final long serialVersionUID = 5137371329342967209L;

	public QuestWorldException() {
	}
	
	@Override
	public String message() {
			return ChatColor.RED + "Quest cannot be completed in this world.";
	}

	@Override
	public String cause() {
		return "";
	}

}
