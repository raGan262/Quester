package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.QuestHolderManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.commandbase.QNestedCommand;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.QuestManager;

public class AdminCommands {

	private Quester plugin = null;
	private ProfileManager profMan = null;
	private QuestManager qMan = null;
	private QuestHolderManager holMan = null;
	
	public AdminCommands(Quester plugin) {
		this.plugin = plugin;
		this.profMan = plugin.getProfileManager();
		this.qMan = plugin.getQuestManager();
		this.holMan = plugin.getHolderManager();
	}
	
	@QCommandLabels({"save"})
	@QCommand(
			section = "Admin",
			desc = "saves quests and profiles",
			max = 0,
			usage = "(-qph)",
			permission = QConfiguration.PERM_ADMIN)
	public void save(QCommandContext context, CommandSender sender) {
		boolean pro, que, hol;
		pro = context.hasFlag('p');
		que = context.hasFlag('q');
		hol = context.hasFlag('h');
		if(pro || que || hol) {
			if(que) qMan.saveQuests();
			if(pro) profMan.saveProfiles();
			if(hol) holMan.saveHolders();
		}
		else {
			profMan.saveProfiles();
			qMan.saveQuests();
			holMan.saveHolders();
		}
		
		sender.sendMessage(context.getSenderLang().MSG_DATA_SAVE);
	}
	
	@QCommandLabels({"startsave"})
	@QCommand(
			section = "Admin",
			desc = "starts scheduled profile saving",
			max = 0,
			permission = QConfiguration.PERM_ADMIN)
	public void startsave(QCommandContext context, CommandSender sender) {
		if(QConfiguration.saveInterval == 0) {
			sender.sendMessage(ChatColor.RED + context.getSenderLang().MSG_AUTOSAVE_DISABLED);
			return;
		}
		if(plugin.startSaving()) {
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().MSG_AUTOSAVE_STARTED
					.replaceAll("%interval", String.valueOf(QConfiguration.saveInterval)));
		} else {
			sender.sendMessage(ChatColor.RED + context.getSenderLang().MSG_AUTOSAVE_RUNNING);
		}
	}

	@QCommandLabels({"stopsave"})
	@QCommand(
			section = "Admin",
			desc = "stops scheduled profile saving",
			max = 0,
			permission = QConfiguration.PERM_ADMIN)
	public void stopsave(QCommandContext context, CommandSender sender) {
		if(QConfiguration.saveInterval == 0) {
			sender.sendMessage(ChatColor.RED + context.getSenderLang().MSG_AUTOSAVE_DISABLED);
			return;
		}
		if(plugin.stopSaving()) {
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().MSG_AUTOSAVE_STOPPED);
		} else {
			sender.sendMessage(ChatColor.RED + context.getSenderLang().MSG_AUTOSAVE_NOT_RUNNING);
		}
	}

	@QCommandLabels({"reload"})
	@QCommand(
			section = "Admin",
			desc = "reloads config and local file",
			max = 0,
			permission = QConfiguration.PERM_ADMIN)
	public void reload(QCommandContext context, CommandSender sender) {
		// TODO reloading once loading is done
		//DataManager.reloadData();
		//plugin.reloadLocal();
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().MSG_CONFIG_RELOADED);
	}

	@QCommandLabels({"version", "ver"})
	@QCommand(
			section = "Admin",
			desc = "version info",
			max = 0,
			permission = QConfiguration.PERM_ADMIN)
	public void version(QCommandContext context, CommandSender sender) {
		sender.sendMessage(Quester.LABEL + ChatColor.GOLD + "version " + plugin.getDescription().getVersion());
		sender.sendMessage(Quester.LABEL + plugin.getDescription().getWebsite());
		sender.sendMessage(Quester.LABEL + ChatColor.GRAY + "made by " + plugin.getDescription().getAuthors().get(0));
	}
	
	@QCommandLabels({"player"})
	@QCommand(
			section = "Admin",
			desc = "player profile modification commands")
	@QNestedCommand(PlayerCommands.class)
	public void player(QCommandContext context, CommandSender sender) {
	}
}
