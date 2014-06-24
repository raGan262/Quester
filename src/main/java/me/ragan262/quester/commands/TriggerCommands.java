package me.ragan262.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.QCommandLabels;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.elements.ElementManager;
import me.ragan262.quester.elements.ElementManager.ElementType;
import me.ragan262.quester.elements.Trigger;
import me.ragan262.quester.exceptions.ElementException;
import me.ragan262.quester.exceptions.ObjectiveException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.exceptions.TriggerException;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.QuestManager;

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
	
	private Trigger getTrigger(final String type, final QCommandContext subContext, final QuesterLang lang) throws QCommandException, ObjectiveException, QuesterException {
		if(!eMan.elementExists(ElementType.TRIGGER, type)) {
			subContext.getSender().sendMessage(ChatColor.RED + lang.get("ERROR_TRIG_NOT_EXIST"));
			subContext.getSender().sendMessage(
					ChatColor.RED + lang.get("TRIG_LIST") + ": " + ChatColor.WHITE
							+ eMan.getElementList(ElementType.TRIGGER));
			throw new TriggerException(lang.get("ERROR_TRIG_NOT_EXIST"));
		}
		final Trigger trig =
				(Trigger) eMan.getElementFromCommand(ElementType.TRIGGER, type, subContext);
		if(trig == null) {
			throw new ElementException(lang.get("ERROR_ELEMENT_FAIL"));
		}
		else {
			trig.setGlobal(subContext.hasFlag('g'));
		}
		return trig;
	}
	
	@QCommandLabels({ "add", "a" })
	@QCommand(section = "QMod", desc = "adds a trigger", min = 1, usage = "<trig type> [args]")
	public void add(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
		final QuesterLang lang = context.getSenderLang();
		final String type = context.getString(0);
		Trigger trig;
		try {
			trig = getTrigger(type, context.getSubContext(1), lang);
		}
		catch (final TriggerException e) {
			return;
		}
		qMan.addQuestTrigger(profMan.getProfile(sender.getName()), trig, lang);
		sender.sendMessage(ChatColor.GREEN
				+ lang.get("TRIG_ADD").replaceAll("%type", type.toUpperCase()));
	}
	
	@QCommandLabels({ "set", "s" })
	@QCommand(
			section = "QMod",
			desc = "sets a trigger",
			min = 2,
			usage = "<trig ID> <trig type> [args]")
	public void set(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
		final QuesterLang lang = context.getSenderLang();
		final String type = context.getString(2);
		final int triggerID = context.getInt(0);
		Trigger trig;
		try {
			trig = getTrigger(type, context.getSubContext(3), lang);
		}
		catch (final TriggerException e) {
			return;
		}
		qMan.setQuestTrigger(profMan.getProfile(sender.getName()), triggerID, trig, lang);
		sender.sendMessage(ChatColor.GREEN
				+ lang.get("TRIG_SET").replaceAll("%type", type.toUpperCase()));
	}
	
	@QCommandLabels({ "remove", "r" })
	@QCommand(section = "QMod", desc = "removes trigger", min = 1, max = 1, usage = "<trig ID>")
	public void remove(final QCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.removeQuestTrigger(profMan.getProfile(sender.getName()), context.getInt(0),
				context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().get("TRIG_REMOVE")
						.replaceAll("%id", context.getString(0)));
	}
	
	@QCommandLabels({ "list", "l" })
	@QCommand(section = "QMod", max = 0, desc = "trigger list")
	public void list(final QCommandContext context, final CommandSender sender) throws QuesterException {
		sender.sendMessage(ChatColor.RED + context.getSenderLang().get("TRIG_LIST") + ": "
				+ ChatColor.WHITE + eMan.getElementList(ElementType.TRIGGER));
	}
}
