package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.commandbase.exceptions.QPermissionException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.managers.DataManager;
import com.gmail.molnardad.quester.managers.QuestManager;
import com.gmail.molnardad.quester.utils.Util;

public class UserCommands {

	private QuestManager qMan = null;
	
	public UserCommands(Quester plugin) {
		qMan = plugin.getQuestManager();
	}
	
	@QCommandLabels({"show"})
	@QCommand(
			desc = "shows info about the quest",
			max = 1,
			min = 1,
			usage = "\"<quest name>\"",
			permission = DataManager.PERM_USE_SHOW)
	public void show(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.showQuest(sender, context.getString(0));
	}
	
	@QCommandLabels({"list"})
	@QCommand(
			desc = "displays quest list",
			max = 0,
			permission = DataManager.PERM_USE_LIST)
	public void list(QCommandContext context, CommandSender sender) {
		if(Util.permCheck(sender, DataManager.PERM_MODIFY, false, null)) {
			qMan.showFullQuestList(sender);
		}
		else {
			qMan.showQuestList(sender);
		}
	}
	
	@QCommandLabels({"profile"})
	@QCommand(
			desc = "shows player's profile",
			max = 1,
			usage = "[player]",
			permission = DataManager.PERM_USE_PROFILE)
	public void profile(QCommandContext context, CommandSender sender) throws QuesterException {
		if(Util.permCheck(sender, DataManager.PERM_ADMIN, false, null) && context.length() > 0) {
			qMan.showProfile(sender, context.getString(0));
		}
		else {
			qMan.showProfile(sender);
		}
	}
	
	@QCommandLabels({"start"})
	@QCommand(
			desc = "starts the quest",
			max = 1,
			usage = "\"[quest name]\"")
	public void start(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().MSG_ONLY_PLAYER);
			return;
		}
		if(context.length() == 0) {
			if(Util.permCheck(sender, DataManager.PERM_USE_START_RANDOM, false, null)) {
				qMan.startRandomQuest(context.getPlayer());
			}
			else {
				throw new QPermissionException();
			}
		}
		else if(Util.permCheck(sender, DataManager.PERM_USE_START_PICK, false, null)) {
			qMan.startQuest((Player) sender, context.getString(0), true);
		}
		else {
			throw new QPermissionException();
		}
	}
	
	@QCommandLabels({"done"})
	@QCommand(
			desc = "completes current objective",
			max = 0,
			permission = DataManager.PERM_USE_DONE)
	public void done(QCommandContext context, CommandSender sender) throws QuesterException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().MSG_ONLY_PLAYER);
			return;
		}
		qMan.complete((Player) sender, true);
	}

	@QCommandLabels({"cancel"})
	@QCommand(
			desc = "completes current objective",
			max = 1,
			usage = "[quest ID]",
			permission = DataManager.PERM_USE_CANCEL)
	public void cancel(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().MSG_ONLY_PLAYER);
			return;
		}
		int index = -1;
		if(context.length() > 0) {
			index = context.getInt(0);
		}
		qMan.cancelQuest((Player) sender, index, true);
	}
	
	@QCommandLabels({"switch"})
	@QCommand(
			desc = "switches current quest",
			max = 1,
			min = 1,
			usage = "<index>",
			permission = DataManager.PERM_USE_SWITCH)
	public void switch0(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().MSG_ONLY_PLAYER);
			return;
		}
		if (qMan.switchQuest((Player) sender, context.getInt(0))) {
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_SWITCHED);
		}
	}

	@QCommandLabels({"progress", "prog"})
	@QCommand(
			desc = "shows quest progress",
			max = 1,
			usage = "[index]",
			permission = DataManager.PERM_USE_PROGRESS)
	public void progress(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().MSG_ONLY_PLAYER);
			return;
		}
		int index = -1;
		if(context.length() > 0) {
			index = context.getInt(0);
		}
		qMan.showProgress((Player) sender, index);
	}
	
	@QCommandLabels({"quests"})
	@QCommand(
			desc = "shows player's profile",
			max = 1,
			usage = "[player]",
			permission = DataManager.PERM_USE_QUESTS)
	public void quests(QCommandContext context, CommandSender sender) throws QuesterException {
		if(Util.permCheck(sender, DataManager.PERM_ADMIN, false, null) && context.length() > 0) {
			qMan.showTakenQuests(sender, context.getString(0));
		}
		else {
			qMan.showTakenQuests(sender);
		}
	}
}