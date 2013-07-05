package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.commandbase.QNestedCommand;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.ElementManager;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.exceptions.ElementException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.quests.QuestManager;
import com.gmail.molnardad.quester.strings.QuesterLang;

public class ObjectiveCommands {
	
	QuestManager qMan = null;
	ElementManager eMan = null;
	
	public ObjectiveCommands(Quester plugin) {
		qMan = plugin.getQuestManager();
		eMan = plugin.getElementManager();
	}
	
	@QCommandLabels({"add", "a"})
	@QCommand(
			section = "QMod",
			desc = "adds an objective",
			min = 1,
			usage = "<objective type> [args]")
	public void add(QCommandContext context, CommandSender sender) throws QCommandException, QuesterException {
		QuesterLang lang = context.getSenderLang();
		String type = context.getString(0);
		if(!eMan.isObjective(type)) {
			sender.sendMessage(ChatColor.RED + lang.ERROR_OBJ_NOT_EXIST);
			sender.sendMessage(ChatColor.RED + lang.OBJ_LIST + ": "
					+ ChatColor.WHITE + eMan.getObjectiveList());
			return;
		}
		Objective obj = eMan.getObjectiveFromCommand(type, context.getSubContext(1));
		if(obj == null) {
			throw new ElementException(lang.ERROR_ELEMENT_FAIL);
		}
		if(context.hasFlag('h')) {
			obj.setHidden(true);
		}
		qMan.addQuestObjective(sender.getName(), obj, context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + lang.OBJ_ADD.replaceAll("%type", type.toUpperCase()));
	}
	
	@QCommandLabels({"remove", "r"})
	@QCommand(
			section = "QMod",
			desc = "removes objective",
			min = 1,
			max = 1,
			usage = "<objective ID>")
	public void remove(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.removeQuestObjective(sender.getName(), context.getInt(0), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().OBJ_REMOVE.replaceAll("%id", context.getString(0)));
	}
	
	@QCommandLabels({"list", "l"})
	@QCommand(
			section = "QMod",
			max = 0,
			desc = "objective list")
	public void list(QCommandContext context, CommandSender sender) throws QuesterException {
		sender.sendMessage(ChatColor.RED + context.getSenderLang().OBJ_LIST + ": "
				+ ChatColor.WHITE + eMan.getObjectiveList());	
	}
	
	@QCommandLabels({"swap"})
	@QCommand(
			section = "QMod",
			min = 2,
			max = 2,
			desc = "swaps two objectives",
			usage = "<obj ID 1> <obj ID 2>")
	public void swap(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.swapQuestObjectives(sender.getName(), context.getInt(0), context.getInt(1), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().OBJ_SWAP
				.replaceAll("%id1", context.getString(0)).replaceAll("%id2", context.getString(1)));
	}
	
	@QCommandLabels({"move"})
	@QCommand(
			section = "QMod",
			min = 2,
			max = 2,
			desc = "moves an objective",
			usage = "<ID from> <ID to>")
	public void move(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.moveQuestObjective(sender.getName(), context.getInt(0), context.getInt(1), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().OBJ_MOVE
				.replaceAll("%id1", context.getString(0)).replaceAll("%id2", context.getString(1)));
	}
	
	@QCommandLabels({"desc"})
	@QCommand(
			section = "QMod",
			desc = "objective description manipulation")
	@QNestedCommand(ObjectiveDescCommands.class)
	public void desc(QCommandContext context, CommandSender sender) throws QuesterException {
	}
	
	@QCommandLabels({"prereq"})
	@QCommand(
			section = "QMod",
			desc = "objective prerequisites manipulation")
	@QNestedCommand(ObjectivePrereqCommands.class)
	public void prereq(QCommandContext context, CommandSender sender) throws QuesterException {
	}
}