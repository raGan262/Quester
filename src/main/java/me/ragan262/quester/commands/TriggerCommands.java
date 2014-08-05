package me.ragan262.quester.commands;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.annotations.CommandLabels;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Element;
import me.ragan262.quester.elements.ElementManager;
import me.ragan262.quester.elements.Trigger;
import me.ragan262.quester.exceptions.ElementException;
import me.ragan262.quester.exceptions.ObjectiveException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.exceptions.TriggerException;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.QuestManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TriggerCommands {
	
	final QuestManager qMan;
	final ElementManager eMan;
	final ProfileManager profMan;
	final Quester plugin;
	
	public TriggerCommands(final Quester plugin) {
		qMan = plugin.getQuestManager();
		eMan = plugin.getElementManager();
		profMan = plugin.getProfileManager();
		this.plugin = plugin;
	}
	
	private Trigger getTrigger(final String type, final QuesterCommandContext subContext, final QuesterLang lang) throws CommandException, ObjectiveException, QuesterException {
		if(!eMan.elementExists(Element.TRIGGER, type)) {
			subContext.getSender().sendMessage(ChatColor.RED + lang.get("ERROR_TRIG_NOT_EXIST"));
			subContext.getSender().sendMessage(ChatColor.RED + lang.get("TRIG_LIST") + ": "
					+ ChatColor.WHITE + eMan.getElementList(Element.TRIGGER));
			throw new TriggerException(lang.get("ERROR_TRIG_NOT_EXIST"));
		}
		final Trigger trig = (Trigger)eMan.getElementFromCommand(Element.TRIGGER, type, subContext);
		if(trig == null) {
			throw new ElementException(lang.get("ERROR_ELEMENT_FAIL"));
		}
		else {
			trig.setGlobal(subContext.hasFlag('g'));
		}
		return trig;
	}
	
	@CommandLabels({ "add", "a" })
	@Command(section = "QMod", desc = "adds a trigger", min = 1, usage = "<trig type> [args]")
	public void add(final QuesterCommandContext context, final CommandSender sender) throws CommandException, QuesterException {
		final QuesterLang lang = context.getSenderLang();
		final String type = context.getString(0);
		Trigger trig;
		try {
			trig = getTrigger(type, context.getSubContext(1), lang);
		}
		catch(final TriggerException e) {
			return;
		}
		qMan.addQuestTrigger(profMan.getSenderProfile(sender), trig, lang);
		sender.sendMessage(ChatColor.GREEN
				+ lang.get("TRIG_ADD").replaceAll("%type", type.toUpperCase()));
	}
	
	@CommandLabels({ "set", "s" })
	@Command(
			section = "QMod",
			desc = "sets a trigger",
			min = 2,
			usage = "<trig ID> <trig type> [args]")
	public void set(final QuesterCommandContext context, final CommandSender sender) throws CommandException, QuesterException {
		final QuesterLang lang = context.getSenderLang();
		final String type = context.getString(2);
		final int triggerID = context.getInt(0);
		Trigger trig;
		try {
			trig = getTrigger(type, context.getSubContext(3), lang);
		}
		catch(final TriggerException e) {
			return;
		}
		qMan.setQuestTrigger(profMan.getSenderProfile(sender), triggerID, trig, lang);
		sender.sendMessage(ChatColor.GREEN
				+ lang.get("TRIG_SET").replaceAll("%type", type.toUpperCase()));
	}
	
	@CommandLabels({ "remove", "r" })
	@Command(section = "QMod", desc = "removes trigger", min = 1, max = 1, usage = "<trig ID>")
	public void remove(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.removeQuestTrigger(profMan.getSenderProfile(sender), context.getInt(0), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().get("TRIG_REMOVE").replaceAll("%id", context.getString(0)));
	}
	
	@CommandLabels({ "list", "l" })
	@Command(section = "QMod", max = 0, desc = "trigger list")
	public void list(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		sender.sendMessage(ChatColor.RED + context.getSenderLang().get("TRIG_LIST") + ": "
				+ ChatColor.WHITE + eMan.getElementList(Element.TRIGGER));
	}
}
