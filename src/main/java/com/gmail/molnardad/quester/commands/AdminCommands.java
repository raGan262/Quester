package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.strings.QuesterStrings;

public class AdminCommands {

	private Quester plugin = null;
	private QuesterStrings lang = null;
	
	public AdminCommands(Quester plugin) {
		this.plugin = plugin;
		this.lang = plugin.getLanguageManager().getDefaultLang();
	}
	
	@QCommand(
			labels = {"save"},
			desc = "saves profiles",
			max = 0,
			permission = QuestData.PERM_ADMIN)
	public void save(QCommandContext context, CommandSender sender) {
		Quester.data.saveProfiles();
		sender.sendMessage(lang.MSG_PROFILES_SAVE);
	}
	
	@QCommand(
			labels = {"startsave"},
			desc = "starts scheduled profile saving",
			max = 0,
			permission = QuestData.PERM_ADMIN)
	public void startsave(QCommandContext context, CommandSender sender) {
		if(Quester.data.saveInterval == 0) {
			sender.sendMessage(ChatColor.RED + lang.MSG_AUTOSAVE_DISABLED);
			return;
		}
		if(plugin.startSaving()) {
			sender.sendMessage(ChatColor.GREEN + lang.MSG_AUTOSAVE_STARTED
					.replaceAll("%interval", String.valueOf(Quester.data.saveInterval)));
		} else {
			sender.sendMessage(ChatColor.RED + lang.MSG_AUTOSAVE_RUNNING);
		}
	}
	
	@QCommand(
			labels = {"stopsave"},
			desc = "stops scheduled profile saving",
			max = 0,
			permission = QuestData.PERM_ADMIN)
	public void stopsave(QCommandContext context, CommandSender sender) {
		if(Quester.data.saveInterval == 0) {
			sender.sendMessage(ChatColor.RED + lang.MSG_AUTOSAVE_DISABLED);
			return;
		}
		if(plugin.stopSaving()) {
			sender.sendMessage(ChatColor.GREEN + lang.MSG_AUTOSAVE_STOPPED);
		} else {
			sender.sendMessage(ChatColor.RED + lang.MSG_AUTOSAVE_NOT_RUNNING);
		}
	}
	
	@QCommand(
			labels = {"reload"},
			desc = "reloads config and local file",
			max = 0,
			permission = QuestData.PERM_ADMIN)
	public void reload(QCommandContext context, CommandSender sender) {
		plugin.initializeConfig();
		plugin.reloadLocal();
		sender.sendMessage(ChatColor.GREEN + lang.MSG_CONFIG_RELOADED);
	}
	
	@QCommand(
			labels = {"version", "ver"},
			desc = "version info",
			max = 0,
			permission = QuestData.PERM_ADMIN)
	public void version(QCommandContext context, CommandSender sender) {
		sender.sendMessage(Quester.LABEL + ChatColor.GOLD + "version " + plugin.getDescription().getVersion());
		sender.sendMessage(Quester.LABEL + plugin.getDescription().getWebsite());
		sender.sendMessage(Quester.LABEL + ChatColor.GRAY + "made by " + plugin.getDescription().getAuthors().get(0));
	}
}
