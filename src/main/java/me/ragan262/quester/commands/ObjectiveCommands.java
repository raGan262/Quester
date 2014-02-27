package me.ragan262.quester.commands;

import java.util.Set;

import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.QCommandLabels;
import me.ragan262.quester.commandbase.QNestedCommand;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
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
	
	private Objective getObjective(final String type, final QCommandContext subContext, final QuesterLang lang) throws QCommandException, ObjectiveException, QuesterException {
		if(!eMan.isObjective(type)) {
			subContext.getSender().sendMessage(ChatColor.RED + lang.get("ERROR_OBJ_NOT_EXIST"));
			subContext.getSender().sendMessage(
					ChatColor.RED + lang.get("OBJ_LIST") + ": " + ChatColor.WHITE
							+ eMan.getObjectiveList());
			throw new ObjectiveException(lang.get("ERROR_OBJ_NOT_EXIST"));
		}
		final Objective obj = eMan.getObjectiveFromCommand(type, subContext);
		if(obj == null) {
			throw new ElementException(lang.get("ERROR_ELEMENT_FAIL"));
		}
		else {
			obj.setHidden(subContext.hasFlag('h'));
			obj.setDisplayProgress(!subContext.hasFlag('p'));
		}
		return obj;
	}
	
	@QCommandLabels({ "add", "a" })
	@QCommand(
			section = "QMod",
			desc = "adds an objective",
			min = 1,
			usage = "<objective type> [args] (-h)")
	public void add(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
		final QuesterLang lang = context.getSenderLang();
		final String type = context.getString(0);
		Objective obj;
		try {
			obj = getObjective(type, context.getSubContext(1), lang);
		}
		catch (final ObjectiveException e) {
			return;
		}
		qMan.addQuestObjective(profMan.getProfile(sender.getName()), obj, context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ lang.get("OBJ_ADD").replaceAll("%type", type.toUpperCase()));
	}
	
	@QCommandLabels({ "set", "s" })
	@QCommand(
			section = "QMod",
			desc = "sets an objective",
			min = 2,
			usage = "<obj ID> <obj type> [args] (-h)")
	public void set(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
		final QuesterLang lang = context.getSenderLang();
		final int objectiveID = context.getInt(0);
		final String type = context.getString(1);
		Objective obj;
		try {
			obj = getObjective(type, context.getSubContext(2), lang);
		}
		catch (final ObjectiveException e) {
			return;
		}
		qMan.setQuestObjective(profMan.getProfile(sender.getName()), objectiveID, obj,
				context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ lang.get("OBJ_SET").replaceAll("%type", type.toUpperCase()));
	}
	
	@QCommandLabels({ "remove", "r" })
	@QCommand(
			section = "QMod",
			desc = "removes objective",
			min = 1,
			max = 1,
			usage = "<objective ID>")
	public void remove(final QCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.removeQuestObjective(profMan.getProfile(sender.getName()), context.getInt(0),
				context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().get("OBJ_REMOVE").replaceAll("%id", context.getString(0)));
	}
	
	@QCommandLabels({ "list", "l" })
	@QCommand(section = "QMod", max = 0, desc = "objective list")
	public void list(final QCommandContext context, final CommandSender sender) throws QuesterException {
		sender.sendMessage(ChatColor.RED + context.getSenderLang().get("OBJ_LIST") + ": "
				+ ChatColor.WHITE + eMan.getObjectiveList());
	}
	
	@QCommandLabels({ "swap" })
	@QCommand(
			section = "QMod",
			min = 2,
			max = 2,
			desc = "swaps two objectives",
			usage = "<obj ID 1> <obj ID 2>")
	public void swap(final QCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.swapQuestObjectives(profMan.getProfile(sender.getName()), context.getInt(0),
				context.getInt(1), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().get("OBJ_SWAP").replaceAll("%id1", context.getString(0))
						.replaceAll("%id2", context.getString(1)));
	}
	
	@QCommandLabels({ "move" })
	@QCommand(
			section = "QMod",
			min = 2,
			max = 2,
			desc = "moves an objective",
			usage = "<ID from> <ID to>")
	public void move(final QCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.moveQuestObjective(profMan.getProfile(sender.getName()), context.getInt(0),
				context.getInt(1), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().get("OBJ_MOVE").replaceAll("%id1", context.getString(0))
						.replaceAll("%id2", context.getString(1)));
	}
	
	@QCommandLabels({ "desc" })
	@QCommand(section = "QMod", desc = "objective description manipulation")
	@QNestedCommand(ObjectiveDescCommands.class)
	public void desc(final QCommandContext context, final CommandSender sender) throws QuesterException {
	}
	
	@QCommandLabels({ "prereq" })
	@QCommand(section = "QMod", desc = "objective prerequisites manipulation")
	@QNestedCommand(ObjectivePrereqCommands.class)
	public void prereq(final QCommandContext context, final CommandSender sender) throws QuesterException {
	}
	
	public static class ObjectiveDescCommands {
		
		final QuestManager qMan;
		final ProfileManager profMan;
		
		public ObjectiveDescCommands(final Quester plugin) {
			qMan = plugin.getQuestManager();
			profMan = plugin.getProfileManager();
		}
		
		@QCommandLabels({ "add", "a" })
		@QCommand(
				section = "QMod",
				desc = "adds to objective description",
				min = 2,
				max = 2,
				usage = "<objective ID> <description>")
		public void add(final QCommandContext context, final CommandSender sender) throws QuesterException {
			qMan.addObjectiveDescription(profMan.getProfile(sender.getName()), context.getInt(0),
					context.getString(1), context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("OBJ_DESC_ADD")
							.replaceAll("%id", context.getString(0)));
		}
		
		@QCommandLabels({ "remove", "r" })
		@QCommand(
				section = "QMod",
				desc = "removes objective description",
				min = 1,
				max = 1,
				usage = "<objective ID>")
		public void remove(final QCommandContext context, final CommandSender sender) throws QuesterException {
			qMan.removeObjectiveDescription(profMan.getProfile(sender.getName()),
					context.getInt(0), context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("OBJ_DESC_REMOVE")
							.replaceAll("%id", context.getString(0)));
		}
	}
	
	public static class ObjectivePrereqCommands {
		
		final QuestManager qMan;
		final ProfileManager profMan;
		
		public ObjectivePrereqCommands(final Quester plugin) {
			qMan = plugin.getQuestManager();
			profMan = plugin.getProfileManager();
		}
		
		@QCommandLabels({ "add", "a" })
		@QCommand(
				section = "QMod",
				desc = "adds objective prerequisites",
				min = 1,
				usage = "<objective ID> <prerequisite1>...")
		public void add(final QCommandContext context, final CommandSender sender) throws QuesterException {
			final Set<Integer> prereq = SerUtils.parsePrerequisites(context.getArgs(), 1);
			qMan.addObjectivePrerequisites(profMan.getProfile(sender.getName()), context.getInt(0),
					prereq, context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("OBJ_PREREQ_ADD")
							.replaceAll("%id", context.getString(0)));
		}
		
		@QCommandLabels({ "remove", "r" })
		@QCommand(
				section = "QMod",
				desc = "removes objective prerequisites",
				min = 1,
				usage = "<objective ID> <prerequisite1>...")
		public void remove(final QCommandContext context, final CommandSender sender) throws QuesterException {
			final Set<Integer> prereq = SerUtils.parsePrerequisites(context.getArgs(), 1);
			qMan.removeObjectivePrerequisites(profMan.getProfile(sender.getName()),
					context.getInt(0), prereq, context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("OBJ_PREREQ_REMOVE")
							.replaceAll("%id", context.getString(0)));
		}
	}
}