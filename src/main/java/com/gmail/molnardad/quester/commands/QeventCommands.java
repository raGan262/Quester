package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.ElementManager;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.ElementException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.quests.QuestManager;
import com.gmail.molnardad.quester.strings.QuesterLang;
import com.gmail.molnardad.quester.utils.Util;

public class QeventCommands {
	
	QuestManager qMan = null;
	ElementManager eMan = null;
	
	public QeventCommands(Quester plugin) {
		qMan = plugin.getQuestManager();
		eMan = plugin.getElementManager();
	}
	
	@QCommandLabels({"add", "a"})
	@QCommand(
			section = "QMod",
			desc = "adds an event",
			min = 2,
			usage = "{<occasion>} <event type> [args]")
	public void add(QCommandContext context, CommandSender sender) throws QCommandException, QuesterException {
		QuesterLang lang = context.getSenderLang();
		int[] occasion;
		try {
			occasion = Util.deserializeOccasion(context.getString(0), context.getSenderLang());
		}
		catch (IllegalArgumentException e) {
			throw new QCommandException(e.getMessage());
		}
		String type = context.getString(1);
		if(!eMan.isEvent(type)) {
			sender.sendMessage(ChatColor.RED + lang.ERROR_EVT_NOT_EXIST);
			sender.sendMessage(ChatColor.RED + lang.EVT_LIST + ": "
					+ ChatColor.WHITE + eMan.getEventList());
			return;
		}
		Qevent evt = eMan.getEventFromCommand(type, context.getSubContext(2));
		if(evt == null) {
			throw new ElementException(lang.ERROR_ELEMENT_FAIL);
		}
		evt.setOccasion(occasion[0], occasion[1]);
		qMan.addQevent(sender.getName(), evt, context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + lang.EVT_ADD.replaceAll("%type", type.toUpperCase()));
	}
	
	@QCommandLabels({"remove", "r"})
	@QCommand(
			section = "QMod",
			desc = "removes event",
			min = 1,
			max = 1,
			usage = "<event ID>")
	public void remove(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.removeQevent(sender.getName(), context.getInt(0), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().EVT_REMOVE.replaceAll("%id", context.getString(0)));
	}
	
	@QCommandLabels({"list", "l"})
	@QCommand(
			section = "QMod",
			max = 0,
			desc = "event list")
	public void list(QCommandContext context, CommandSender sender) throws QuesterException {
		sender.sendMessage(ChatColor.RED + context.getSenderLang().EVT_LIST + ": "
				+ ChatColor.WHITE + eMan.getEventList());
	}
}