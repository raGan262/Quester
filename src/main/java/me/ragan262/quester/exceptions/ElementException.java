package me.ragan262.quester.exceptions;

public class ElementException extends QuesterException {
	
	private static final long serialVersionUID = -7898630917380214091L;
	
	public ElementException() {
		super("Invalid element");
	}
	
	public ElementException(final String msg) {
		super(msg);
	}
}
