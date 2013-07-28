package com.gmail.molnardad.quester.exceptions;

public abstract class QuesterException extends Exception {
	
	private static final long serialVersionUID = 2477882018035034147L;
	
	public QuesterException(final String msg) {
		super(msg);
	}
}
