package com.gmail.molnardad.quester.commands;

import static com.gmail.molnardad.quester.utils.Util.parsePrerequisites;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.quests.QuestManager;

public class ObjectivePrereqCommands {
	
	QuestManager qMan = null;
	
	public ObjectivePrereqCommands(Quester plugin) {
		qMan = plugin.getQuestManager();
	}
	
	@QCommandLabels({"add", "a"})
	@QCommand(
			section = "QMod",
			desc = "adds objective prerequisites",
			min = 1,
			usage = "<objective ID> <prerequisite1>...")
	public void add(QCommandContext context, CommandSender sender) throws QuesterException {
		Set<Integer> prereq = parsePrerequisites(context.getArgs(), 1);
		qMan.addObjectivePrerequisites(sender.getName(), context.getInt(0), prereq, context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().OBJ_PREREQ_ADD
				.replaceAll("%id", context.getString(0)));
	}
	
	@QCommandLabels({"remove", "r"})
	@QCommand(
			section = "QMod",
			desc = "removes objective prerequisites",
			min = 1,
			usage = "<objective ID> <prerequisite1>...")
	public void remove(QCommandContext context, CommandSender sender) throws QuesterException {
		Set<Integer> prereq = parsePrerequisites(context.getArgs(), 1);
		qMan.removeObjectivePrerequisites(sender.getName(), context.getInt(0), prereq, context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().OBJ_PREREQ_REMOVE
				.replaceAll("%id", context.getString(0)));
	}
}
