package me.ragan262.quester.commands;

import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.QCommandLabels;
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
	
	@QCommandLabels({ "list" })
	@QCommand(
			section = "Mod",
			desc = "list of custom localized messages",
			max = 1,
			usage = "[language]",
			permission = QConfiguration.PERM_MODIFY)
	public void list(final QCommandContext context, final CommandSender sender) throws QuesterException {
		
	}
	
	@QCommandLabels({ "sync" })
	@QCommand(
			section = "Mod",
			desc = "synchronizes language files and custom message config",
			max = 1,
			usage = "[quest ID]",
			permission = QConfiguration.PERM_MODIFY)
	public void sync(final QCommandContext context, final CommandSender sender) throws QuesterException {
		
	}
	
	@QCommandLabels({ "remove" })
	@QCommand(
			section = "Mod",
			desc = "removes custom message",
			min = 1,
			max = 1,
			usage = "<key>",
			permission = QConfiguration.PERM_MODIFY)
	public void remove(final QCommandContext context, final CommandSender sender) throws QuesterException {
		
	}
	
	@QCommandLabels({ "set" })
	@QCommand(
			section = "Mod",
			desc = "sets custom message",
			min = 2,
			max = 2,
			usage = "<key> <message>",
			permission = QConfiguration.PERM_MODIFY)
	public void set(final QCommandContext context, final CommandSender sender) throws QuesterException {
		
	}
	
	@QCommandLabels({ "get" })
	@QCommand(
			section = "Mod",
			desc = "gets custom message",
			min = 1,
			max = 2,
			usage = "<key> [language]",
			permission = QConfiguration.PERM_MODIFY)
	public void get(final QCommandContext context, final CommandSender sender) throws QuesterException {
		
	}
}
