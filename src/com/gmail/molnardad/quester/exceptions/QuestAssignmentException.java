package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public final class QuestAssignmentException extends QuesterException {

	private static final long serialVersionUID = -4320925896891118639L;

	private final boolean hasQuest;
	private final String cause;
	
	public QuestAssignmentException(String cause, boolean hasQuest) {
		this.hasQuest = hasQuest;
		this.cause = cause;
	}
	
	@Override
	public String message() {
		if(hasQuest) {
			return ChatColor.RED + "Other quest already assigned.";
		} else {
			return ChatColor.RED + "No quest assigned.";
		}
	}

	@Override
	public String cause() {
		return this.cause;
	}

}
