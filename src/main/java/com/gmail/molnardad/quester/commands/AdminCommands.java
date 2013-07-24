package com.gmail.molnardad.quester.commands;

import javax.management.InstanceNotFoundException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.commandbase.QNestedCommand;
import com.gmail.molnardad.quester.holder.QuestHolderManager;
import com.gmail.molnardad.quester.lang.LanguageManager;
import com.gmail.molnardad.quester.profiles.PlayerProfile;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.QuestManager;
import com.gmail.molnardad.quester.utils.Util;

public class AdminCommands {

	private Quester plugin = null;
	private ProfileManager profMan = null;
	private QuestManager qMan = null;
	private QuestHolderManager holMan = null;
	private LanguageManager langMan = null;
	
	public AdminCommands(Quester plugin) {
		this.plugin = plugin;
		this.profMan = plugin.getProfileManager();
		this.qMan = plugin.getQuestManager();
		this.holMan = plugin.getHolderManager();
		this.langMan = plugin.getLanguageManager();
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
	
	@QCommandLabels({"save"})
	@QCommand(
			section = "Admin",
			desc = "saves quests and profiles",
			max = 0,
			usage = "(-hpq)",
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
	
	@QCommandLabels({"reload"})
	@QCommand(
			section = "Admin",
			desc = "reloads quests, config and languages",
			usage = "(-clq)",
			permission = QConfiguration.PERM_ADMIN)
	public void reload(QCommandContext context, CommandSender sender) {
		boolean con, que, lang;
		con = context.hasFlag('c');
		que = context.hasFlag('q');
		lang = context.hasFlag('l');
		if(con || que || lang) {
			if(que) reloadQuests();
			if(con) reloadData();
			if(lang) langMan.reloadLangs();
		}
		else {
			reloadQuests();
			reloadData();
			langMan.reloadLangs();
		}
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().MSG_DATA_RELOADED);
	}

	// used above
	private void reloadData() {
		try {
			QConfiguration.reloadData();
		}
		catch (InstanceNotFoundException e) {
			Quester.log.info("Failed to reload config: No instance of QConfiguration.");
		}
	}
	
	private void reloadQuests() {
		if(qMan.loadQuests()) {
			for(PlayerProfile prof : profMan.getProfiles()) {
				String[] unset = profMan.validateProgress(prof);
				Player player = null;
				if(unset.length > 0 && (player = Bukkit.getServer().getPlayerExact(prof.getName())) != null) {
					player.sendMessage(Quester.LABEL + langMan.getPlayerLang(player.getName()).MSG_Q_SOME_CANCELLED);
					player.sendMessage(Quester.LABEL + ChatColor.WHITE + Util.implode(unset, ','));
				}
			}
		}
		else {
			Quester.log.info("Failed to reload quests.");
		}
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
