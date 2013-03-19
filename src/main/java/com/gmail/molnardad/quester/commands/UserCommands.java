package com.gmail.molnardad.quester.commands;

import java.util.List;
import java.util.Map;

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
import com.gmail.molnardad.quester.managers.ProfileManager;
import com.gmail.molnardad.quester.managers.QuestManager;
import com.gmail.molnardad.quester.utils.Util;

public class UserCommands {

	private QuestManager qMan = null;
	private ProfileManager profMan = null;
	private Quester plugin = null;
	
	public UserCommands(Quester plugin) {
		qMan = plugin.getQuestManager();
		profMan = plugin.getProfileManager();
		this.plugin = plugin;
	}
	
	@QCommandLabels({"help"})
	@QCommand(
			section = "QUser",
			desc = "displays help",
			usage = "[arg1] [arg2]...",
			permission = DataManager.PERM_USE_SHOW)
	public void help(QCommandContext context, CommandSender sender) throws QuesterException {
		Map<String, List<String>> cmds = plugin.getCommandManager().getHelp(context.getArgs(), sender);
		for(String s : cmds.keySet()) {
			sender.sendMessage("---------- Section " + s + " ----------");
			for(String ss : cmds.get(s)) {
				sender.sendMessage(ss);
			}
		}
	}
	
	@QCommandLabels({"show"})
	@QCommand(
			section = "QUser",
			desc = "shows info about the quest",
			max = 1,
			usage = "\"[quest name]\"",
			permission = DataManager.PERM_USE_SHOW)
	public void show(QCommandContext context, CommandSender sender) throws QuesterException {
		String quest = "";
		if(context.length() > 0) {
			quest = context.getString(0);
		}
		qMan.showQuest(sender, quest, context.getSenderLang());
	}
	
	@QCommandLabels({"list"})
	@QCommand(
			section = "QUser",
			desc = "displays quest list",
			max = 0,
			permission = DataManager.PERM_USE_LIST)
	public void list(QCommandContext context, CommandSender sender) {
		if(Util.permCheck(sender, DataManager.PERM_MODIFY, false, null)) {
			qMan.showFullQuestList(sender, context.getSenderLang());
		}
		else {
			qMan.showQuestList(sender, context.getSenderLang());
		}
	}
	
	@QCommandLabels({"profile"})
	@QCommand(
			section = "QUser",
			desc = "shows player's profile",
			max = 1,
			usage = "[player]",
			permission = DataManager.PERM_USE_PROFILE)
	public void profile(QCommandContext context, CommandSender sender) throws QuesterException {
		if(Util.permCheck(sender, DataManager.PERM_ADMIN, false, null) && context.length() > 0) {
			profMan.showProfile(sender, context.getString(0), context.getSenderLang());
		}
		else {
			profMan.showProfile(sender);
		}
	}
	
	@QCommandLabels({"start"})
	@QCommand(
			section = "QUser",
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
				qMan.startRandomQuest(context.getPlayer(), context.getSenderLang());
			}
			else {
				throw new QPermissionException();
			}
		}
		else if(Util.permCheck(sender, DataManager.PERM_USE_START_PICK, false, null)) {
			qMan.startQuest((Player) sender, context.getString(0), true, context.getSenderLang());
		}
		else {
			throw new QPermissionException();
		}
	}
	
	@QCommandLabels({"done"})
	@QCommand(
			section = "QUser",
			desc = "completes current objective",
			max = 0,
			permission = DataManager.PERM_USE_DONE)
	public void done(QCommandContext context, CommandSender sender) throws QuesterException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().MSG_ONLY_PLAYER);
			return;
		}
		qMan.complete((Player) sender, true, context.getSenderLang());
	}

	@QCommandLabels({"cancel"})
	@QCommand(
			section = "QUser",
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
		qMan.cancelQuest((Player) sender, index, true, context.getSenderLang());
	}
	
	@QCommandLabels({"switch"})
	@QCommand(
			section = "QUser",
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
		if (profMan.switchQuest((Player) sender, context.getInt(0))) {
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_SWITCHED);
		}
	}

	@QCommandLabels({"progress", "prog"})
	@QCommand(
			section = "QUser",
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
		qMan.showProgress((Player) sender, index, context.getSenderLang());
	}
	
	@QCommandLabels({"quests"})
	@QCommand(
			section = "QUser",
			desc = "shows player's quests",
			max = 1,
			usage = "[player]",
			permission = DataManager.PERM_USE_QUESTS)
	public void quests(QCommandContext context, CommandSender sender) throws QuesterException {
		if(Util.permCheck(sender, DataManager.PERM_ADMIN, false, null) && context.length() > 0) {
			qMan.showTakenQuests(sender, context.getString(0), context.getSenderLang());
		}
		else {
			qMan.showTakenQuests(sender);
		}
	}
}