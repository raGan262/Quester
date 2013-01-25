package com.gmail.molnardad.quester.commandbase.exceptions;


public class QUsageException extends QCommandException {

	private static final long serialVersionUID = -3357140092874580056L;

	private final String usage;
	
	public QUsageException(String message, String usage) {
		super(message);
		this.usage = usage;
	}
	
	public String getUsage() {
		return this.usage;
	}
}
