package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public final class QuestCompletionException extends QuesterException {

	private static final long serialVersionUID = 5137371329562967209L;

	private final boolean isObjective;
	private final String cause;
	
	public QuestCompletionException(String cause, boolean isObjective) {
		this.cause = cause;
		this.isObjective = isObjective;
	}
	
	@Override
	public String message() {
		if(isObjective) {
			return ChatColor.RED + "One or more objectives are not completed.";
		} else {
			return ChatColor.RED + "One or more rewards cannot be given.";
		}
	}

	@Override
	public String cause() {
		return cause;
	}

}
