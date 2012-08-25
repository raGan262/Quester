package com.gmail.molnardad.quester.exceptions;

import org.bukkit.ChatColor;

public enum ExceptionType {

	CUSTOM(ChatColor.RED + "Something is wrong."),
	
	Q_EXIST(ChatColor.RED + "Quest already exists."),
	Q_NOT_EXIST(ChatColor.RED + "Quest does not exist."),
	Q_NOT_SELECTED(ChatColor.RED + "No quest selected."),
	Q_CANT_MODIFY(ChatColor.RED + "Modification of active quest is not allowed."),
	Q_NONE(ChatColor.RED + "No quest available."),
	Q_NONE_ACTIVE(ChatColor.RED + "No quest active."),
	Q_ASSIGNED(ChatColor.RED + "Other quest already assigned."),
	Q_NOT_ASSIGNED(ChatColor.RED + "No quest assigned."),
	Q_CANT_CANCEL(ChatColor.RED + "This quest cannot be cancelled."),
	Q_NOT_COMPLETED(ChatColor.RED + "One or more objectives are not completed."),
	Q_BAD_WORLD(ChatColor.RED + "Quest cannot be completed in this world."),
	Q_NOT_CMD(ChatColor.RED + "Quest cannot be started or completed by command."),
	
	CON_NOT_MET(ChatColor.RED + "One or more conditions are not met."),
	CON_NOT_EXIST(ChatColor.RED + "Condition does not exist."),
	
	OBJ_NOT_EXIST(ChatColor.RED + "Objective does not exist."),
	OBJ_CANT_DO(ChatColor.RED + "Not enough resources to complete objective."),
	
	OCC_NOT_EXIST(ChatColor.RED + "Occasion does not exist."),
	
	REW_NOT_EXIST(ChatColor.RED + "Reward does not exist."),
	REW_CANT_DO(ChatColor.RED + "Not enough space to recieve quest rewards."),
	
	EVT_NOT_EXIST(ChatColor.RED + "Event does not exist."),
	
	WHY(ChatColor.RED + "Why would you want to do this ?");
	
	private final String message;
	
	private ExceptionType(String message) {
		this.message = message;
	}
	
	public String message() {
		return this.message;
	}
}
