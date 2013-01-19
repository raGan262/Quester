package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.commandbase.exceptions.QPermissionException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.strings.QuesterStrings;
import com.gmail.molnardad.quester.utils.Util;

public class UserCommands {

	private QuestManager qMan = null;
	private QuesterStrings lang = null;
	
	public UserCommands(Quester plugin) {
		this.qMan = plugin.getQuestManager();
		this.lang = plugin.getLanguageManager().getDefaultLang();
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
			min = 1,
			usage = "[quest_ID]",
			permission = QuestData.PERM_MODIFY)
	public void info(QCommandContext context, CommandSender sender) throws QuesterException {
		if(context.length() > 0) {
			qMan.showQuestInfo(sender, context.getInt(0));
		}
		else {
			qMan.showQuestInfo(sender);
		}
	}
	
	@QCommand(
			labels = {"list"},
			desc = "displays quest list",
			max = 0,
			permission = QuestData.PERM_USE_LIST)
	public void list(QCommandContext context, CommandSender sender) {
		if(Util.permCheck(sender, QuestData.PERM_MODIFY, false)) {
			qMan.showFullQuestList(sender);
		}
		else {
			qMan.showQuestList(sender);
		}
	}
	
	@QCommand(
			labels = {"profile"},
			desc = "shows player's profile",
			max = 1,
			usage = "[player]",
			permission = QuestData.PERM_USE_PROFILE)
	public void profile(QCommandContext context, CommandSender sender) throws QuesterException {
		if(Util.permCheck(sender, QuestData.PERM_ADMIN, false) && context.length() > 0) {
			qMan.showProfile(sender, context.getString(0));
		}
		else {
			qMan.showProfile(sender);
		}
	}
	
	@QCommand(
			labels = {"start"},
			desc = "starts the quest",
			max = 1,
			usage = "\"[quest name]\"")
	public void start(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		if(context.getPlayer() == null) {
			sender.sendMessage(lang.MSG_ONLY_PLAYER);
			return;
		}
		if(context.length() == 0) {
			if(Util.permCheck(sender, QuestData.PERM_USE_START_RANDOM, false)) {
				qMan.startRandomQuest(context.getPlayer());
			}
			else {
				throw new QPermissionException();
			}
		}
		else if(Util.permCheck(sender, QuestData.PERM_USE_START_PICK, false)) {
			qMan.startQuest((Player) sender, context.getString(0), true);
		}
		else {
			throw new QPermissionException();
		}
	}
	
	@QCommand(
			labels = {"done"},
			desc = "completes current objective",
			max = 0,
			permission = QuestData.PERM_USE_DONE)
	public void done(QCommandContext context, CommandSender sender) throws QuesterException {
		if(context.getPlayer() == null) {
			sender.sendMessage(lang.MSG_ONLY_PLAYER);
			return;
		}
		qMan.complete((Player) sender, true);
	}

	@QCommand(
			labels = {"cancel"},
			desc = "completes current objective",
			max = 1,
			usage = "[quest ID]",
			permission = QuestData.PERM_USE_CANCEL)
	public void cancel(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		if(context.getPlayer() == null) {
			sender.sendMessage(lang.MSG_ONLY_PLAYER);
			return;
		}
		int index = -1;
		if(context.length() > 0) {
			index = context.getInt(0);
		}
		qMan.cancelQuest((Player) sender, index, true);
	}
	
	@QCommand(
			labels = {"switch"},
			desc = "switches current quest",
			max = 1,
			min = 1,
			usage = "<index>",
			permission = QuestData.PERM_USE_SWITCH)
	public void switch0(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		if(context.getPlayer() == null) {
			sender.sendMessage(lang.MSG_ONLY_PLAYER);
			return;
		}
		if (qMan.switchQuest((Player) sender, context.getInt(0))) {
			sender.sendMessage(ChatColor.GREEN + lang.Q_SWITCHED);
		}
	}

	@QCommand(
			labels = {"progress", "prog"},
			desc = "shows quest progress",
			max = 1,
			usage = "[index]",
			permission = QuestData.PERM_USE_PROGRESS)
	public void progress(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		if(context.getPlayer() == null) {
			sender.sendMessage(lang.MSG_ONLY_PLAYER);
			return;
		}
		int index = -1;
		if(context.length() > 0) {
			index = context.getInt(0);
		}
		qMan.showProgress((Player) sender, index);
	}
	
	@QCommand(
			labels = {"quests"},
			desc = "shows player's profile",
			max = 1,
			usage = "[player]",
			permission = QuestData.PERM_USE_QUESTS)
	public void quests(QCommandContext context, CommandSender sender) throws QuesterException {
		if(Util.permCheck(sender, QuestData.PERM_ADMIN, false) && context.length() > 0) {
			qMan.showTakenQuests(sender, context.getString(0));
		}
		else {
			qMan.showTakenQuests(sender);
		}
	}
}