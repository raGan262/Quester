package me.ragan262.quester.commandbase.exceptions;

public class QCommandException extends Exception {
	
	private static final long serialVersionUID = -952512667733427700L;
	
	public QCommandException() {
		super();
	}
	
	public QCommandException(final String message) {
		super(message);
	}
}
