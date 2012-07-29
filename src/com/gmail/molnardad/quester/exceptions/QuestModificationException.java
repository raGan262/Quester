package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public final class QuestModificationException extends QuesterException {

	private static final long serialVersionUID = -5364110967475236290L;
	private final boolean canModify;
	private final String cause;
	
	public QuestModificationException(String cause, boolean canModify) {
		this.cause = cause;
		this.canModify = canModify;
	}
	
	@Override
	public String message() {
		if(canModify) {
			return ChatColor.RED + "No quest selected.";
		} else {
			return ChatColor.RED + "Modification of active quest is not allowed.";
		}
	}

	@Override
	public String cause() {
		return cause;
	}

}
