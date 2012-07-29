package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public final class QuestAvailabilityException extends QuesterException {

	private static final long serialVersionUID = 3961551014588257049L;
	private final boolean availability;
	private final String cause;
	
	public QuestAvailabilityException(String cause, boolean availability) {
		this.cause = cause;
		this.availability = availability;
	}
	
	@Override
	public String message() {
		if(availability) {
			return ChatColor.RED + "No quest available.";
		} else {
			return ChatColor.RED + "No quest active.";
		}
	}

	@Override
	public String cause() {
		return cause;
	}

}
