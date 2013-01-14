package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public class QuesterException extends Exception {

	private static final long serialVersionUID = 2477882018035034147L;

	private final ExceptionType type;
	
	public QuesterException(ExceptionType exT) {
		super(exT.message());
		type = exT;
	}
	
	public QuesterException(String msg) {
		super(ChatColor.RED + msg);
		type = ExceptionType.CUSTOM;
	}
	
	public ExceptionType type() {
		return type;
	}
}
