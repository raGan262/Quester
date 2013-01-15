package com.gmail.molnardad.quester.commands;

import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.utils.Util;

public class UserCommands {

	private QuestManager qMan = null;
	
	public UserCommands(Quester plugin) {
		this.qMan = Quester.qMan;
	}
	
	@QCommand(
			labels = {"show"},
			desc = "shows info about the quest",
			max = 1,
			min = 1,
			usage = "\"<quest name>\"",
			permission = QuestData.PERM_USE_SHOW)
	public void show(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.showQuest(sender, context.getString(0));
	}
	
	@QCommand(
			labels = {"info"},
			desc = "shows detailed info about the quest",
			max = 1,
			usage = "[ID]",
			permission = QuestData.MODIFY_PERM)
	public void info(QCommandContext context, CommandSender sender) throws QuesterException {
	}
	
	@QCommand(
			labels = {"list"},
			desc = "displays quest list",
			max = 0,
			permission = QuestData.PERM_USE_LIST)
	public void list(QCommandContext context, CommandSender sender) {
		sender.sendMessage("LIST COMMAND EXECUTION !");
		sender.sendMessage(Util.implode(context.getAllArgs()));
	}
}
