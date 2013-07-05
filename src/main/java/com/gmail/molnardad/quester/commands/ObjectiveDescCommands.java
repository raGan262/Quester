package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.quests.QuestManager;

public class ObjectiveDescCommands {
	
	QuestManager qMan = null;
	
	public ObjectiveDescCommands(Quester plugin) {
		qMan = plugin.getQuestManager();
	}
	
	@QCommandLabels({"add", "a"})
	@QCommand(
			section = "QMod",
			desc = "adds to objective description",
			min = 2,
			max = 2,
			usage = "<objective ID> <description>")
	public void add(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.addObjectiveDescription(sender.getName(), context.getInt(0), context.getString(1), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().OBJ_DESC_ADD.replaceAll("%id", context.getString(0)));
	}
	
	@QCommandLabels({"remove", "r"})
	@QCommand(
			section = "QMod",
			desc = "removes objective description",
			min = 1,
			max = 1,
			usage = "<objective ID>")
	public void remove(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.removeObjectiveDescription(sender.getName(), context.getInt(0), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().OBJ_DESC_REMOVE.replaceAll("%id", context.getString(0)));
	}
}
