package me.ragan262.quester.commands;

import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.QCommandLabels;
import me.ragan262.quester.commandbase.QNestedCommand;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.elements.Condition;
import me.ragan262.quester.elements.ElementManager;
import me.ragan262.quester.exceptions.ConditionException;
import me.ragan262.quester.exceptions.ElementException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.QuestManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
			subContext.getSender().sendMessage(ChatColor.RED + lang.get("ERROR_CON_NOT_EXIST"));
			subContext.getSender().sendMessage(
					ChatColor.RED + lang.get("CON_LIST") + ": " + ChatColor.WHITE
							+ eMan.getConditionList());
			throw new ConditionException(lang.get("ERROR_CON_NOT_EXIST"));
		}
		final Condition con = eMan.getConditionFromCommand(type, subContext);
		if(con == null) {
			throw new ElementException(lang.get("ERROR_ELEMENT_FAIL"));
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
		qMan.addQuestCondition(profMan.getSenderProfile(sender), condition, lang);
		sender.sendMessage(ChatColor.GREEN
				+ lang.get("CON_ADD").replaceAll("%type", type.toUpperCase()));
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
		qMan.setQuestCondition(profMan.getSenderProfile(sender), conditionID, condition, lang);
		sender.sendMessage(ChatColor.GREEN
				+ lang.get("CON_SET").replaceAll("%type", type.toUpperCase()));
	}
	
	@QCommandLabels({ "remove", "r" })
	@QCommand(
			section = "QMod",
			desc = "removes condition",
			min = 1,
			max = 1,
			usage = "<condition ID>")
	public void remove(final QCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.removeQuestCondition(profMan.getSenderProfile(sender), context.getInt(0),
				context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().get("CON_REMOVE").replaceAll("%id", context.getString(0)));
	}
	
	@QCommandLabels({ "desc" })
	@QCommand(section = "QMod", desc = "condition description manipulation")
	@QNestedCommand(ConditionDescCommands.class)
	public void desc(final QCommandContext context, final CommandSender sender) throws QuesterException {}
	
	@QCommandLabels({ "list", "l" })
	@QCommand(section = "QMod", max = 0, desc = "condition list")
	public void list(final QCommandContext context, final CommandSender sender) throws QuesterException {
		sender.sendMessage(ChatColor.RED + context.getSenderLang().get("CON_LIST") + ": "
				+ ChatColor.WHITE + eMan.getConditionList());
	}
	
	public static class ConditionDescCommands {
		
		final QuestManager qMan;
		final ProfileManager profMan;
		
		public ConditionDescCommands(final Quester plugin) {
			qMan = plugin.getQuestManager();
			profMan = plugin.getProfileManager();
		}
		
		@QCommandLabels({ "add", "a" })
		@QCommand(
				section = "QMod",
				desc = "adds to condition description",
				min = 2,
				max = 2,
				usage = "<condition ID> <description>")
		public void add(final QCommandContext context, final CommandSender sender) throws QuesterException {
			qMan.addConditionDescription(profMan.getSenderProfile(sender), context.getInt(0),
					context.getString(1), context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("CON_DESC_ADD")
							.replaceAll("%id", context.getString(0)));
		}
		
		@QCommandLabels({ "remove", "r" })
		@QCommand(
				section = "QMod",
				desc = "clears condition description",
				min = 1,
				max = 1,
				usage = "<condition ID>")
		public void remove(final QCommandContext context, final CommandSender sender) throws QuesterException {
			qMan.removeConditionDescription(profMan.getSenderProfile(sender), context.getInt(0),
					context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("CON_DESC_REMOVE")
							.replaceAll("%id", context.getString(0)));
		}
	}
}