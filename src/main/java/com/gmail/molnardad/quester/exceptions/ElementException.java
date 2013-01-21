package com.gmail.molnardad.quester.exceptions;

public class ElementException extends Exception {

	private static final long serialVersionUID = -7898630917380214091L;
	
	public ElementException() {
		super("Invalid element");
	}
	
	public ElementException(String msg) {
		super();
	}
}
