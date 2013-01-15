package com.gmail.molnardad.quester.commandbase.exceptions;

public class QPermissionException extends QCommandException {

	private static final long serialVersionUID = 5628739617988598981L;

	public QPermissionException() {
		super("You don't have permission for this.");
	}
}
