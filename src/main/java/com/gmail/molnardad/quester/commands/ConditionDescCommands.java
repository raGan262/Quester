package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.quests.QuestManager;

public class ConditionDescCommands {
	
	QuestManager qMan = null;
	
	public ConditionDescCommands(Quester plugin) {
		qMan = plugin.getQuestManager();
	}
	
	@QCommandLabels({"add", "a"})
	@QCommand(
			section = "QMod",
			desc = "adds condition description",
			min = 2,
			max = 2,
			usage = "<condition ID> <description>")
	public void add(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.addConditionDescription(sender.getName(), context.getInt(0), context.getString(1), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().CON_DESC_ADD.replaceAll("%id", context.getString(0)));
	}
	
	@QCommandLabels({"remove", "r"})
	@QCommand(
			section = "QMod",
			desc = "adds to condition description",
			min = 1,
			max = 1,
			usage = "<condition ID>")
	public void remove(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.removeConditionDescription(sender.getName(), context.getInt(0), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().CON_DESC_REMOVE.replaceAll("%id", context.getString(0)));
	}
}
