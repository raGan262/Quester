package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public final class QuestCancellationException extends QuesterException {

	private static final long serialVersionUID = -4320925896891118639L;

	public QuestCancellationException() {
	}
	
	@Override
	public String message() {
		return ChatColor.RED + "This quest cannot be cancelled.";
	}

	@Override
	public String cause() {
		return "";
	}

}
