package me.ragan262.quester.commands;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.annotations.CommandLabels;
import me.ragan262.commandmanager.annotations.NestedCommand;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Condition;
import me.ragan262.quester.elements.ElementManager;
import me.ragan262.quester.elements.ElementManager.ElementType;
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
	
	private Condition getCondition(final String type, final QuesterCommandContext subContext, final QuesterLang lang) throws ConditionException, CommandException, QuesterException {
		
		if(!eMan.elementExists(ElementType.CONDITION, type)) {
			subContext.getSender().sendMessage(ChatColor.RED + lang.get("ERROR_CON_NOT_EXIST"));
			subContext.getSender().sendMessage(
					ChatColor.RED + lang.get("CON_LIST") + ": " + ChatColor.WHITE
							+ eMan.getElementList(ElementType.CONDITION));
			throw new ConditionException(lang.get("ERROR_CON_NOT_EXIST"));
		}
		final Condition con =
				(Condition) eMan.getElementFromCommand(ElementType.CONDITION, type, subContext);
		if(con == null) {
			throw new ElementException(lang.get("ERROR_ELEMENT_FAIL"));
		}
		return con;
	}
	
	@CommandLabels({ "add", "a" })
	@Command(
			section = "QMod",
			desc = "adds a condition",
			min = 1,
			usage = "<condition type> [args]")
	public void add(final QuesterCommandContext context, final CommandSender sender) throws CommandException, QuesterException {
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
	
	@CommandLabels({ "set", "s" })
	@Command(
			section = "QMod",
			desc = "adds a condition",
			min = 2,
			usage = "<con ID> <con type> [args]")
	public void set(final QuesterCommandContext context, final CommandSender sender) throws CommandException, QuesterException {
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
	
	@CommandLabels({ "remove", "r" })
	@Command(
			section = "QMod",
			desc = "removes condition",
			min = 1,
			max = 1,
			usage = "<condition ID>")
	public void remove(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.removeQuestCondition(profMan.getSenderProfile(sender), context.getInt(0),
				context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().get("CON_REMOVE").replaceAll("%id", context.getString(0)));
	}
	
	@CommandLabels({ "desc" })
	@Command(section = "QMod", desc = "condition description manipulation")
	@NestedCommand(ConditionDescCommands.class)
	public void desc(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {}
	
	@CommandLabels({ "list", "l" })
	@Command(section = "QMod", max = 0, desc = "condition list")
	public void list(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		sender.sendMessage(ChatColor.RED + context.getSenderLang().get("CON_LIST") + ": "
				+ ChatColor.WHITE + eMan.getElementList(ElementType.CONDITION));
	}
	
	public static class ConditionDescCommands {
		
		final QuestManager qMan;
		final ProfileManager profMan;
		
		public ConditionDescCommands(final Quester plugin) {
			qMan = plugin.getQuestManager();
			profMan = plugin.getProfileManager();
		}
		
		@CommandLabels({ "add", "a" })
		@Command(
				section = "QMod",
				desc = "adds to condition description",
				min = 2,
				max = 2,
				usage = "<condition ID> <description>")
		public void add(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			qMan.addConditionDescription(profMan.getSenderProfile(sender), context.getInt(0),
					context.getString(1), context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("CON_DESC_ADD")
							.replaceAll("%id", context.getString(0)));
		}
		
		@CommandLabels({ "remove", "r" })
		@Command(
				section = "QMod",
				desc = "clears condition description",
				min = 1,
				max = 1,
				usage = "<condition ID>")
		public void remove(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			qMan.removeConditionDescription(profMan.getSenderProfile(sender), context.getInt(0),
					context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("CON_DESC_REMOVE")
							.replaceAll("%id", context.getString(0)));
		}
	}
}
