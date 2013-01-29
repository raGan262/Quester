package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.managers.DataManager;

public class AdminCommands {

	private Quester plugin = null;
	
	public AdminCommands(Quester plugin) {
		this.plugin = plugin;
	}
	
	@QCommandLabels({"save"})
	@QCommand(
			desc = "saves profiles",
			max = 0,
			permission = DataManager.PERM_ADMIN)
	public void save(QCommandContext context, CommandSender sender) {
		DataManager.getInstance().saveProfiles();
		sender.sendMessage(context.getSenderLang().MSG_PROFILES_SAVE);
	}
	
	@QCommandLabels({"startsave"})
	@QCommand(
			desc = "starts scheduled profile saving",
			max = 0,
			permission = DataManager.PERM_ADMIN)
	public void startsave(QCommandContext context, CommandSender sender) {
		if(DataManager.getInstance().saveInterval == 0) {
			sender.sendMessage(ChatColor.RED + context.getSenderLang().MSG_AUTOSAVE_DISABLED);
			return;
		}
		if(plugin.startSaving()) {
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().MSG_AUTOSAVE_STARTED
					.replaceAll("%interval", String.valueOf(DataManager.getInstance().saveInterval)));
		} else {
			sender.sendMessage(ChatColor.RED + context.getSenderLang().MSG_AUTOSAVE_RUNNING);
		}
	}

	@QCommandLabels({"stopsave"})
	@QCommand(
			desc = "stops scheduled profile saving",
			max = 0,
			permission = DataManager.PERM_ADMIN)
	public void stopsave(QCommandContext context, CommandSender sender) {
		if(DataManager.getInstance().saveInterval == 0) {
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
			desc = "reloads config and local file",
			max = 0,
			permission = DataManager.PERM_ADMIN)
	public void reload(QCommandContext context, CommandSender sender) {
		plugin.initializeConfig();
		plugin.reloadLocal();
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().MSG_CONFIG_RELOADED);
	}

	@QCommandLabels({"version", "ver"})
	@QCommand(
			desc = "version info",
			max = 0,
			permission = DataManager.PERM_ADMIN)
	public void version(QCommandContext context, CommandSender sender) {
		sender.sendMessage(Quester.LABEL + ChatColor.GOLD + "version " + plugin.getDescription().getVersion());
		sender.sendMessage(Quester.LABEL + plugin.getDescription().getWebsite());
		sender.sendMessage(Quester.LABEL + ChatColor.GRAY + "made by " + plugin.getDescription().getAuthors().get(0));
	}
}
