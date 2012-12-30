package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public class QuesterException extends Exception {

	private static final long serialVersionUID = 2477882018035034147L;

	private final ExceptionType type;
	private final String message;
	
	public QuesterException(ExceptionType exT) {
		type = exT;
		message = exT.message();
	}
	
	public QuesterException(String msg) {
		type = ExceptionType.CUSTOM;
		message = ChatColor.RED + msg;
	}
	
	public String message() {
		return message;
	}
	
	public ExceptionType type() {
		return type;
	}
}
