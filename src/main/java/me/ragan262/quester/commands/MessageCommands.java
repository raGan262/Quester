package me.ragan262.quester.commands;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.annotations.CommandLabels;
import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.LanguageManager;

import org.bukkit.command.CommandSender;

public class MessageCommands {
	
	final LanguageManager langMan;
	
	public MessageCommands(final Quester plugin) {
		langMan = plugin.getLanguageManager();
	}
	
	// TODO
	// LIST
	// SYNC - synchronizes custom messages in config and individual languages
	// REMOVE - "ALL" to remove all
	// SET
	// GET
	
	@CommandLabels({ "list" })
	@Command(
			section = "Mod",
			desc = "list of custom localized messages",
			max = 1,
			usage = "[language]",
			permission = QConfiguration.PERM_MODIFY)
	public void list(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		
	}
	
	@CommandLabels({ "sync" })
	@Command(
			section = "Mod",
			desc = "synchronizes language files and custom message config",
			max = 1,
			usage = "[quest ID]",
			permission = QConfiguration.PERM_MODIFY)
	public void sync(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		
	}
	
	@CommandLabels({ "remove" })
	@Command(
			section = "Mod",
			desc = "removes custom message",
			min = 1,
			max = 1,
			usage = "<key>",
			permission = QConfiguration.PERM_MODIFY)
	public void remove(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		
	}
	
	@CommandLabels({ "set" })
	@Command(
			section = "Mod",
			desc = "sets custom message",
			min = 2,
			max = 2,
			usage = "<key> <message>",
			permission = QConfiguration.PERM_MODIFY)
	public void set(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		
	}
	
	@CommandLabels({ "get" })
	@Command(
			section = "Mod",
			desc = "gets custom message",
			min = 1,
			max = 2,
			usage = "<key> [language]",
			permission = QConfiguration.PERM_MODIFY)
	public void get(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		
	}
}
