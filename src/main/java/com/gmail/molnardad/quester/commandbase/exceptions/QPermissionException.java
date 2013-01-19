package com.gmail.molnardad.quester.commandbase.exceptions;

import com.gmail.molnardad.quester.Quester;

public class QPermissionException extends QCommandException {

	private static final long serialVersionUID = 5628739617988598981L;

	public QPermissionException() {
		super(Quester.plugin.getLanguageManager().getDefaultLang().MSG_PERMS);
	}
}
