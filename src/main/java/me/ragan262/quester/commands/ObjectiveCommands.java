package me.ragan262.quester.commands;

import java.util.Set;
import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.annotations.CommandLabels;
import me.ragan262.commandmanager.annotations.NestedCommand;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Element;
import me.ragan262.quester.elements.ElementManager;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.exceptions.ElementException;
import me.ragan262.quester.exceptions.ObjectiveException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.utils.SerUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ObjectiveCommands {
	
	final QuestManager qMan;
	final ElementManager eMan;
	final ProfileManager profMan;
	
	public ObjectiveCommands(final Quester plugin) {
		qMan = plugin.getQuestManager();
		eMan = plugin.getElementManager();
		profMan = plugin.getProfileManager();
	}
	
	private Objective getObjective(final String type, final QuesterCommandContext subContext, final QuesterLang lang) throws CommandException, ObjectiveException, QuesterException {
		if(!eMan.elementExists(Element.OBJECTIVE, type)) {
			subContext.getSender().sendMessage(ChatColor.RED + lang.get("ERROR_OBJ_NOT_EXIST"));
			subContext.getSender().sendMessage(ChatColor.RED + lang.get("OBJ_LIST") + ": "
					+ ChatColor.WHITE + eMan.getElementList(Element.OBJECTIVE));
			throw new ObjectiveException(lang.get("ERROR_OBJ_NOT_EXIST"));
		}
		final Objective obj = (Objective)eMan.getElementFromCommand(Element.OBJECTIVE, type, subContext);
		if(obj == null) {
			throw new ElementException(lang.get("ERROR_ELEMENT_FAIL"));
		}
		else {
			obj.setHidden(subContext.hasFlag('h'));
			obj.setDisplayProgress(!subContext.hasFlag('p'));
		}
		return obj;
	}
	
	@CommandLabels({ "add", "a" })
	@Command(
			section = "QMod",
			desc = "adds an objective",
			min = 1,
			usage = "<objective type> [args] (-h)")
	public void add(final QuesterCommandContext context, final CommandSender sender) throws CommandException, QuesterException {
		final QuesterLang lang = context.getSenderLang();
		final String type = context.getString(0);
		Objective obj;
		try {
			obj = getObjective(type, context.getSubContext(1), lang);
		}
		catch(final ObjectiveException e) {
			return;
		}
		qMan.addQuestObjective(profMan.getSenderProfile(sender), obj, context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ lang.get("OBJ_ADD").replaceAll("%type", type.toUpperCase()));
	}
	
	@CommandLabels({ "set", "s" })
	@Command(
			section = "QMod",
			desc = "sets an objective",
			min = 2,
			usage = "<obj ID> <obj type> [args] (-h)")
	public void set(final QuesterCommandContext context, final CommandSender sender) throws CommandException, QuesterException {
		final QuesterLang lang = context.getSenderLang();
		final int objectiveID = context.getInt(0);
		final String type = context.getString(1);
		Objective obj;
		try {
			obj = getObjective(type, context.getSubContext(2), lang);
		}
		catch(final ObjectiveException e) {
			return;
		}
		qMan.setQuestObjective(profMan.getSenderProfile(sender), objectiveID, obj, context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ lang.get("OBJ_SET").replaceAll("%type", type.toUpperCase()));
	}
	
	@CommandLabels({ "remove", "r" })
	@Command(
			section = "QMod",
			desc = "removes objective",
			min = 1,
			max = 1,
			usage = "<objective ID>")
	public void remove(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.removeQuestObjective(profMan.getSenderProfile(sender), context.getInt(0), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().get("OBJ_REMOVE").replaceAll("%id", context.getString(0)));
	}
	
	@CommandLabels({ "list", "l" })
	@Command(section = "QMod", max = 0, desc = "objective list")
	public void list(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		sender.sendMessage(ChatColor.RED + context.getSenderLang().get("OBJ_LIST") + ": "
				+ ChatColor.WHITE + eMan.getElementList(Element.OBJECTIVE));
	}
	
	@CommandLabels({ "swap" })
	@Command(
			section = "QMod",
			min = 2,
			max = 2,
			desc = "swaps two objectives",
			usage = "<obj ID 1> <obj ID 2>")
	public void swap(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.swapQuestObjectives(profMan.getSenderProfile(sender), context.getInt(0), context.getInt(1), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().get("OBJ_SWAP").replaceAll("%id1", context.getString(0)).replaceAll("%id2", context.getString(1)));
	}
	
	@CommandLabels({ "move" })
	@Command(
			section = "QMod",
			min = 2,
			max = 2,
			desc = "moves an objective",
			usage = "<ID from> <ID to>")
	public void move(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.moveQuestObjective(profMan.getSenderProfile(sender), context.getInt(0), context.getInt(1), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().get("OBJ_MOVE").replaceAll("%id1", context.getString(0)).replaceAll("%id2", context.getString(1)));
	}
	
	@CommandLabels({ "desc" })
	@Command(section = "QMod", desc = "objective description manipulation")
	@NestedCommand(ObjectiveDescCommands.class)
	public void desc(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {}
	
	@CommandLabels({ "prereq" })
	@Command(section = "QMod", desc = "objective prerequisites manipulation")
	@NestedCommand(ObjectivePrereqCommands.class)
	public void prereq(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {}
	
	@CommandLabels({ "trigger", "trig" })
	@Command(section = "QMod", desc = "objective triggers manipulation")
	@NestedCommand(ObjectiveTrigCommands.class)
	public void trig(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {}
	
	public static class ObjectiveDescCommands {
		
		final QuestManager qMan;
		final ProfileManager profMan;
		
		public ObjectiveDescCommands(final Quester plugin) {
			qMan = plugin.getQuestManager();
			profMan = plugin.getProfileManager();
		}
		
		@CommandLabels({ "add", "a" })
		@Command(
				section = "QMod",
				desc = "adds to objective description",
				min = 2,
				max = 2,
				usage = "<objective ID> <description>")
		public void add(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			qMan.addObjectiveDescription(profMan.getSenderProfile(sender), context.getInt(0), context.getString(1), context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("OBJ_DESC_ADD").replaceAll("%id", context.getString(0)));
		}
		
		@CommandLabels({ "remove", "r" })
		@Command(
				section = "QMod",
				desc = "removes objective description",
				min = 1,
				max = 1,
				usage = "<objective ID>")
		public void remove(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			qMan.removeObjectiveDescription(profMan.getSenderProfile(sender), context.getInt(0), context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("OBJ_DESC_REMOVE").replaceAll("%id", context.getString(0)));
		}
	}
	
	public static class ObjectivePrereqCommands {
		
		final QuestManager qMan;
		final ProfileManager profMan;
		
		public ObjectivePrereqCommands(final Quester plugin) {
			qMan = plugin.getQuestManager();
			profMan = plugin.getProfileManager();
		}
		
		@CommandLabels({ "add", "a" })
		@Command(
				section = "QMod",
				desc = "adds objective prerequisites",
				min = 2,
				usage = "<objective ID> <prerequisite1>...")
		public void add(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			final Set<Integer> prereq = SerUtils.parseIntSet(context.getArgs(), 1);
			qMan.addObjectivePrerequisites(profMan.getSenderProfile(sender), context.getInt(0), prereq, context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("OBJ_PREREQ_ADD").replaceAll("%id", context.getString(0)));
		}
		
		@CommandLabels({ "remove", "r" })
		@Command(
				section = "QMod",
				desc = "removes objective prerequisites",
				min = 2,
				usage = "<objective ID> <prerequisite1>...")
		public void remove(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			final Set<Integer> prereq = SerUtils.parseIntSet(context.getArgs(), 1);
			qMan.removeObjectivePrerequisites(profMan.getSenderProfile(sender), context.getInt(0), prereq, context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("OBJ_PREREQ_REMOVE").replaceAll("%id", context.getString(0)));
		}
	}
	
	public static class ObjectiveTrigCommands {
		
		final QuestManager qMan;
		final ProfileManager profMan;
		
		public ObjectiveTrigCommands(final Quester plugin) {
			qMan = plugin.getQuestManager();
			profMan = plugin.getProfileManager();
		}
		
		@CommandLabels({ "add", "a" })
		@Command(
				section = "QMod",
				desc = "adds objective triggers",
				min = 2,
				usage = "<objective ID> <trigger ID1>...")
		public void add(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			final Set<Integer> prereq = SerUtils.parseIntSet(context.getArgs(), 1);
			qMan.addObjectiveTriggers(profMan.getSenderProfile(sender), context.getInt(0), prereq, context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("OBJ_TRIG_ADD").replaceAll("%id", context.getString(0)));
		}
		
		@CommandLabels({ "remove", "r" })
		@Command(
				section = "QMod",
				desc = "removes objective triggers",
				min = 2,
				usage = "<objective ID> <trigger ID1>...")
		public void remove(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			final Set<Integer> prereq = SerUtils.parseIntSet(context.getArgs(), 1);
			qMan.removeObjectiveTriggers(profMan.getSenderProfile(sender), context.getInt(0), prereq, context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("OBJ_TRIG_REMOVE").replaceAll("%id", context.getString(0)));
		}
	}
}
