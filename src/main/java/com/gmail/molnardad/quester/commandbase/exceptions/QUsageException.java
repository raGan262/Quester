package com.gmail.molnardad.quester.commandbase.exceptions;


public class QUsageException extends QCommandException {

	private static final long serialVersionUID = -3357140092874580056L;

	/**
	 * @uml.property  name="usage"
	 */
	private final String usage;
	
	public QUsageException(String message, String usage) {
		super(message);
		this.usage = usage;
	}
	
	/**
	 * @return
	 * @uml.property  name="usage"
	 */
	public String getUsage() {
		return this.usage;
	}
}
