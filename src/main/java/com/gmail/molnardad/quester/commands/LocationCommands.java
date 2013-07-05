package com.gmail.molnardad.quester.commands;

import static com.gmail.molnardad.quester.utils.Util.getLoc;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.quests.QuestManager;

public class LocationCommands {
	
	QuestManager qMan = null;
	
	public LocationCommands(Quester plugin) {
		qMan = plugin.getQuestManager();
	}
	
	@QCommandLabels({"set", "s"})
	@QCommand(
			section = "QMod",
			desc = "sets quest location",
			min = 2,
			max = 2,
			usage = "{<location>} <range>")
	public void set(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		try {
		int range = context.getInt(1);
		if(range < 1) {
			throw new NumberFormatException();
		}
		qMan.setQuestLocation(sender.getName(), getLoc(sender, context.getString(0)), range, context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_LOC_SET);
		}
		catch (NumberFormatException e) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_RANGE_INVALID);
		}
		catch (IllegalArgumentException e) {
			throw new QCommandException(e.getMessage());
		}
	}
	
	@QCommandLabels({"remove", "r"})
	@QCommand(
			section = "QMod",
			desc = "removes quest location",
			max = 0)
	public void remove(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.removeQuestLocation(sender.getName(), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_LOC_REMOVED);
	}
}
