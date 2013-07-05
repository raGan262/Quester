package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.commandbase.QNestedCommand;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.ElementManager;
import com.gmail.molnardad.quester.exceptions.ElementException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.quests.QuestManager;
import com.gmail.molnardad.quester.strings.QuesterLang;

public class ConditionCommands {
	
	QuestManager qMan = null;
	ElementManager eMan = null;
	
	public ConditionCommands(Quester plugin) {
		qMan = plugin.getQuestManager();
		eMan = plugin.getElementManager();
	}
	
	@QCommandLabels({"add", "a"})
	@QCommand(
			section = "QMod",
			desc = "adds a condition",
			min = 1,
			usage = "<condition type> [args]")
	public void add(QCommandContext context, CommandSender sender) throws QCommandException, QuesterException {
		QuesterLang lang = context.getSenderLang();
		String type = context.getString(0);
		if(!eMan.isCondition(type)) {
			sender.sendMessage(ChatColor.RED + lang.ERROR_CON_NOT_EXIST);
			sender.sendMessage(ChatColor.RED + lang.CON_LIST + ": "
					+ ChatColor.WHITE + eMan.getConditionList());
			return;
		}
		Condition con = eMan.getConditionFromCommand(type, context.getSubContext(1));
		if(con == null) {
			throw new ElementException(lang.ERROR_ELEMENT_FAIL);
		}
		qMan.addQuestCondition(sender.getName(), con, context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + lang.CON_ADD.replaceAll("%type", type.toUpperCase()));
	}
	
	@QCommandLabels({"remove", "r"})
	@QCommand(
			section = "QMod",
			desc = "removes condition",
			min = 1,
			max = 1,
			usage = "<condition ID>")
	public void remove(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.removeQuestCondition(sender.getName(), context.getInt(0), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().CON_REMOVE.replaceAll("%id", context.getString(0)));
	}
	
	@QCommandLabels({"desc"})
	@QCommand(
			section = "QMod",
			desc = "condition description manipulation")
	@QNestedCommand(ConditionDescCommands.class)
	public void desc(QCommandContext context, CommandSender sender) throws QuesterException {
	}
	
	@QCommandLabels({"list", "l"})
	@QCommand(
			section = "QMod",
			max = 0,
			desc = "condition list")
	public void list(QCommandContext context, CommandSender sender) throws QuesterException {
		sender.sendMessage(ChatColor.RED + context.getSenderLang().CON_LIST + ": "
				+ ChatColor.WHITE + eMan.getConditionList());
	}
}