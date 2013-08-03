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
import com.gmail.molnardad.quester.exceptions.ConditionException;
import com.gmail.molnardad.quester.exceptions.ElementException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.lang.QuesterLang;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.QuestManager;

public class ConditionCommands {
	
	final QuestManager qMan;
	final ElementManager eMan;
	final ProfileManager profMan;
	
	public ConditionCommands(final Quester plugin) {
		qMan = plugin.getQuestManager();
		eMan = plugin.getElementManager();
		profMan = plugin.getProfileManager();
	}
	
	private Condition getCondition(final String type, final QCommandContext subContext, final QuesterLang lang) throws ConditionException, QCommandException, QuesterException {
		
		if(!eMan.isCondition(type)) {
			subContext.getSender().sendMessage(ChatColor.RED + lang.ERROR_CON_NOT_EXIST);
			subContext.getSender().sendMessage(
					ChatColor.RED + lang.CON_LIST + ": " + ChatColor.WHITE
							+ eMan.getConditionList());
			throw new ConditionException(lang.ERROR_CON_NOT_EXIST);
		}
		final Condition con = eMan.getConditionFromCommand(type, subContext);
		if(con == null) {
			throw new ElementException(lang.ERROR_ELEMENT_FAIL);
		}
		return con;
	}
	
	@QCommandLabels({ "add", "a" })
	@QCommand(
			section = "QMod",
			desc = "adds a condition",
			min = 1,
			usage = "<condition type> [args]")
	public void add(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
		final QuesterLang lang = context.getSenderLang();
		final String type = context.getString(0);
		Condition condition;
		try {
			condition = getCondition(type, context.getSubContext(1), lang);
		}
		catch (final ConditionException e) {
			return;
		}
		qMan.addQuestCondition(profMan.getProfile(sender.getName()), condition, lang);
		sender.sendMessage(ChatColor.GREEN + lang.CON_ADD.replaceAll("%type", type.toUpperCase()));
	}
	
	@QCommandLabels({ "set", "s" })
	@QCommand(
			section = "QMod",
			desc = "adds a condition",
			min = 2,
			usage = "<con ID> <con type> [args]")
	public void set(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
		final QuesterLang lang = context.getSenderLang();
		final int conditionID = context.getInt(0);
		final String type = context.getString(1);
		Condition condition;
		try {
			condition = getCondition(type, context.getSubContext(2), lang);
		}
		catch (final ConditionException e) {
			return;
		}
		qMan.setQuestCondition(profMan.getProfile(sender.getName()), conditionID, condition, lang);
		sender.sendMessage(ChatColor.GREEN + lang.CON_SET.replaceAll("%type", type.toUpperCase()));
	}
	
	@QCommandLabels({ "remove", "r" })
	@QCommand(
			section = "QMod",
			desc = "removes condition",
			min = 1,
			max = 1,
			usage = "<condition ID>")
	public void remove(final QCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.removeQuestCondition(profMan.getProfile(sender.getName()), context.getInt(0),
				context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().CON_REMOVE.replaceAll("%id", context.getString(0)));
	}
	
	@QCommandLabels({ "desc" })
	@QCommand(section = "QMod", desc = "condition description manipulation")
	@QNestedCommand(ConditionDescCommands.class)
	public void desc(final QCommandContext context, final CommandSender sender) throws QuesterException {
	}
	
	@QCommandLabels({ "list", "l" })
	@QCommand(section = "QMod", max = 0, desc = "condition list")
	public void list(final QCommandContext context, final CommandSender sender) throws QuesterException {
		sender.sendMessage(ChatColor.RED + context.getSenderLang().CON_LIST + ": "
				+ ChatColor.WHITE + eMan.getConditionList());
	}
}