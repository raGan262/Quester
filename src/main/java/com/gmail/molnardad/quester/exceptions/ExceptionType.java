package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

import com.gmail.molnardad.quester.Quester;

public enum ExceptionType {

	CUSTOM(ChatColor.RED + Quester.strings.ERROR_CUSTOM),
	
	Q_EXIST(ChatColor.RED + Quester.strings.ERROR_Q_EXIST),
	Q_NOT_EXIST(ChatColor.RED + Quester.strings.ERROR_Q_NOT_EXIST),
	Q_NOT_SELECTED(ChatColor.RED + Quester.strings.ERROR_Q_NOT_SELECTED),
	Q_CANT_MODIFY(ChatColor.RED + Quester.strings.ERROR_Q_CANT_MODIFY),
	Q_NONE(ChatColor.RED + Quester.strings.ERROR_Q_NONE),
	Q_NONE_ACTIVE(ChatColor.RED + Quester.strings.ERROR_Q_NONE_ACTIVE),
	Q_ASSIGNED(ChatColor.RED + Quester.strings.ERROR_Q_ASSIGNED),
	Q_NOT_ASSIGNED(ChatColor.RED + Quester.strings.ERROR_Q_NOT_ASSIGNED),
	Q_CANT_CANCEL(ChatColor.RED + Quester.strings.ERROR_Q_CANT_CANCEL),
	Q_NOT_COMPLETED(ChatColor.RED + Quester.strings.ERROR_Q_NOT_COMPLETED),
	Q_BAD_WORLD(ChatColor.RED + Quester.strings.ERROR_Q_BAD_WORLD),
	Q_NOT_CMD(ChatColor.RED + Quester.strings.ERROR_Q_NOT_CMD),
	Q_MAX_AMOUNT(ChatColor.RED + Quester.strings.ERROR_Q_MAX_AMOUNT),
	
	HOL_NOT_EXIST(ChatColor.RED + Quester.strings.ERROR_HOL_NOT_EXIST),
	HOL_NOT_SELECTED(ChatColor.RED + Quester.strings.ERROR_HOL_NOT_SELECTED),
	
	CON_NOT_EXIST(ChatColor.RED + Quester.strings.ERROR_CON_NOT_EXIST),
	
	OBJ_NOT_EXIST(ChatColor.RED + Quester.strings.ERROR_OBJ_NOT_EXIST),
	OBJ_CANT_DO(ChatColor.RED + Quester.strings.ERROR_OBJ_CANT_DO),
	
	OCC_NOT_EXIST(ChatColor.RED + Quester.strings.ERROR_OCC_NOT_EXIST),
	
	EVT_NOT_EXIST(ChatColor.RED + Quester.strings.ERROR_EVT_NOT_EXIST),
	
	WHY(ChatColor.RED + Quester.strings.ERROR_WHY);
	
	private final String message;
	
	private ExceptionType(String message) {
		this.message = message;
	}
	
	public String message() {
		return this.message;
	}
}
