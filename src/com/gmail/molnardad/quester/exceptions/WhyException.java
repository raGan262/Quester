package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public final class WhyException extends QuesterException {

	private static final long serialVersionUID = 5178371486562967209L;

	
	public WhyException() {
	}
	
	@Override
	public String message() {
		return ChatColor.RED + "Why would you even want to do that ???";
	}

	@Override
	public String cause() {
		return "";
	}

}
