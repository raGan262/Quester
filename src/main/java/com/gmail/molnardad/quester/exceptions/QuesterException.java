package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public abstract class QuesterException extends Exception {

 	private static final long serialVersionUID = 2477882018035034147L;
 	
	public QuesterException(String msg) {
		super(ChatColor.RED + msg);
	}
}
