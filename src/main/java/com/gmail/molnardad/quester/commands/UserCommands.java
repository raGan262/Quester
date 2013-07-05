package com.gmail.molnardad.quester.commands;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.commandbase.exceptions.QPermissionException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.QuestManager;
import com.gmail.molnardad.quester.strings.QuesterLang;
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
			section = "User",
			desc = "displays help",
			usage = "[arg1] [arg2]...")
	public void help(QCommandContext context, CommandSender sender) throws QuesterException {
		Map<String, List<String>> cmds = plugin.getCommandManager().getHelp(context.getArgs(), sender);
		QuesterLang lang = context.getSenderLang();
		StringBuilder sb;
		String key = "User";
		if(cmds.containsKey(key)) {
			sb = new StringBuilder();
			sb.append(Util.line(ChatColor.BLUE, lang.HELP_SECTION_USE, ChatColor.GOLD)).append(ChatColor.RESET).append('\n');
			for(String ss : cmds.get(key)) {
				sb.append(ss).append(ChatColor.RESET).append('\n');
			}
			cmds.remove(key);
			sender.sendMessage(sb.toString());
		}
		key = "Mod";
		if(cmds.containsKey(key)) {
			sb = new StringBuilder();
			sb.append(Util.line(ChatColor.BLUE, lang.HELP_SECTION_MODIFY, ChatColor.GOLD)).append(ChatColor.RESET).append('\n');
			for(String ss : cmds.get(key)) {
				sb.append(ss).append(ChatColor.RESET).append('\n');
			}
			cmds.remove(key);
			sender.sendMessage(sb.toString());
		}
		key = "QMod";
		if(cmds.containsKey(key)) {
			sb = new StringBuilder();
			sb.append(Util.line(ChatColor.DARK_GRAY, lang.HELP_SECTION_MODIFY_SELECTED)).append(ChatColor.RESET).append('\n');
			for(String ss : cmds.get(key)) {
				sb.append(ss).append(ChatColor.RESET).append('\n');
			}
			cmds.remove(key);
			sender.sendMessage(sb.toString());
		}
		key = "HMod";
		if(cmds.containsKey(key)) {
			sb = new StringBuilder();
			sb.append(Util.line(ChatColor.DARK_GRAY, lang.HELP_SECTION_MODIFY_HOLDER_SELECTED)).append(ChatColor.RESET).append('\n');
			for(String ss : cmds.get(key)) {
				sb.append(ss).append(ChatColor.RESET).append('\n');
			}
			cmds.remove(key);
			sender.sendMessage(sb.toString());
		}
		key = "Admin";
		if(cmds.containsKey(key)) {
			sb = new StringBuilder();
			sb.append(Util.line(ChatColor.BLUE, lang.HELP_SECTION_ADMIN, ChatColor.GOLD)).append(ChatColor.RESET).append('\n');
			for(String ss : cmds.get(key)) {
				sb.append(ss).append(ChatColor.RESET).append('\n');
			}
			cmds.remove(key);
			sender.sendMessage(sb.toString());
		}
		for(String s : cmds.keySet()) {
			sb = new StringBuilder();
			sb.append(Util.line(ChatColor.BLUE, lang.HELP_SECTION_OTHER, ChatColor.GOLD)).append(ChatColor.RESET).append('\n');
			for(String ss : cmds.get(s)) {
				sb.append(ss).append(ChatColor.RESET).append('\n');
			}
			sender.sendMessage(sb.toString());
		}
	}
	
	@QCommandLabels({"show"})
	@QCommand(
			section = "User",
			desc = "shows info about the quest",
			max = 1,
			usage = "\"[quest name]\"",
			permission = QConfiguration.PERM_USE_SHOW)
	public void show(QCommandContext context, CommandSender sender) throws QuesterException {
		String quest = "";
		if(context.length() > 0) {
			quest = context.getString(0);
		}
		qMan.showQuest(sender, quest, context.getSenderLang());
	}
	
	@QCommandLabels({"list"})
	@QCommand(
			section = "User",
			desc = "displays quest list",
			max = 0,
			permission = QConfiguration.PERM_USE_LIST)
	public void list(QCommandContext context, CommandSender sender) {
		if(Util.permCheck(sender, QConfiguration.PERM_MODIFY, false, null)) {
			qMan.showFullQuestList(sender, context.getSenderLang());
		}
		else {
			qMan.showQuestList(sender, context.getSenderLang());
		}
	}
	
	@QCommandLabels({"profile"})
	@QCommand(
			section = "User",
			desc = "shows player's profile",
			max = 1,
			usage = "[player]",
			permission = QConfiguration.PERM_USE_PROFILE)
	public void profile(QCommandContext context, CommandSender sender) throws QuesterException {
		if(Util.permCheck(sender, QConfiguration.PERM_ADMIN, false, null) && context.length() > 0) {
			profMan.showProfile(sender, context.getString(0), context.getSenderLang());
		}
		else {
			profMan.showProfile(sender);
		}
	}
	
	@QCommandLabels({"start"})
	@QCommand(
			section = "User",
			desc = "starts the quest",
			max = 1,
			usage = "\"[quest name]\"",
			permission = QConfiguration.PERM_USE_START_RANDOM+"||"+QConfiguration.PERM_USE_START_PICK)
	public void start(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().MSG_ONLY_PLAYER);
			return;
		}
		ActionSource as = ActionSource.commandSource(sender);
		if(context.length() == 0) {
			if(Util.permCheck(sender, QConfiguration.PERM_USE_START_RANDOM, false, null)) {
				profMan.startRandomQuest(context.getPlayer(), as, context.getSenderLang());
			}
			else {
				throw new QPermissionException();
			}
		}
		else if(Util.permCheck(sender, QConfiguration.PERM_USE_START_PICK, false, null)) {
			profMan.startQuest((Player) sender, context.getString(0), as, context.getSenderLang());
		}
		else {
			throw new QPermissionException();
		}
	}
	
	@QCommandLabels({"done"})
	@QCommand(
			section = "User",
			desc = "completes current objective",
			max = 0,
			permission = QConfiguration.PERM_USE_DONE)
	public void done(QCommandContext context, CommandSender sender) throws QuesterException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().MSG_ONLY_PLAYER);
			return;
		}
		ActionSource as = ActionSource.commandSource(sender);
		profMan.complete((Player) sender, as, context.getSenderLang());
	}

	@QCommandLabels({"cancel"})
	@QCommand(
			section = "User",
			desc = "completes current objective",
			max = 1,
			usage = "[quest ID]",
			permission = QConfiguration.PERM_USE_CANCEL)
	public void cancel(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().MSG_ONLY_PLAYER);
			return;
		}
		int index = -1;
		if(context.length() > 0) {
			index = context.getInt(0);
		}
		ActionSource as = ActionSource.commandSource(sender);
		profMan.cancelQuest((Player) sender, index, as, context.getSenderLang());
	}
	
	@QCommandLabels({"switch"})
	@QCommand(
			section = "User",
			desc = "switches current quest",
			max = 1,
			min = 1,
			usage = "<index>",
			permission = QConfiguration.PERM_USE_SWITCH)
	public void switch0(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().MSG_ONLY_PLAYER);
			return;
		}
		if (profMan.switchQuest(sender.getName(), context.getInt(0))) {
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_SWITCHED);
		}
	}

	@QCommandLabels({"progress", "prog"})
	@QCommand(
			section = "User",
			desc = "shows quest progress",
			max = 1,
			usage = "[index]",
			permission = QConfiguration.PERM_USE_PROGRESS)
	public void progress(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().MSG_ONLY_PLAYER);
			return;
		}
		int index = -1;
		if(context.length() > 0) {
			index = context.getInt(0);
		}
		profMan.showProgress((Player) sender, index, context.getSenderLang());
	}
	
	@QCommandLabels({"quests"})
	@QCommand(
			section = "User",
			desc = "shows player's quests",
			max = 1,
			usage = "[player]",
			permission = QConfiguration.PERM_USE_QUESTS)
	public void quests(QCommandContext context, CommandSender sender) throws QuesterException {
		if(Util.permCheck(sender, QConfiguration.PERM_ADMIN, false, null) && context.length() > 0) {
			profMan.showTakenQuests(sender, context.getString(0), context.getSenderLang());
		}
		else {
			profMan.showTakenQuests(sender);
		}
	}
}