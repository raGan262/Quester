package com.gmail.molnardad.quester.commands;

import static com.gmail.molnardad.quester.utils.Util.parsePrerequisites;

import java.util.Set;

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
import com.gmail.molnardad.quester.exceptions.ObjectiveException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.lang.QuesterLang;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.QuestManager;

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
			subContext.getSender().sendMessage(ChatColor.RED + lang.ERROR_OBJ_NOT_EXIST);
			subContext.getSender().sendMessage(
					ChatColor.RED + lang.OBJ_LIST + ": " + ChatColor.WHITE
							+ eMan.getObjectiveList());
			throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
		}
		final Objective obj = eMan.getObjectiveFromCommand(type, subContext);
		if(obj == null) {
			throw new ElementException(lang.ERROR_ELEMENT_FAIL);
		}
		else if(subContext.hasFlag('h')) {
			obj.setHidden(true);
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
		sender.sendMessage(ChatColor.GREEN + lang.OBJ_ADD.replaceAll("%type", type.toUpperCase()));
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
		sender.sendMessage(ChatColor.GREEN + lang.OBJ_SET.replaceAll("%type", type.toUpperCase()));
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
				+ context.getSenderLang().OBJ_REMOVE.replaceAll("%id", context.getString(0)));
	}
	
	@QCommandLabels({ "list", "l" })
	@QCommand(section = "QMod", max = 0, desc = "objective list")
	public void list(final QCommandContext context, final CommandSender sender) throws QuesterException {
		sender.sendMessage(ChatColor.RED + context.getSenderLang().OBJ_LIST + ": "
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
				+ context.getSenderLang().OBJ_SWAP.replaceAll("%id1", context.getString(0))
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
				+ context.getSenderLang().OBJ_MOVE.replaceAll("%id1", context.getString(0))
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
					+ context.getSenderLang().OBJ_DESC_ADD.replaceAll("%id", context.getString(0)));
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
					+ context.getSenderLang().OBJ_DESC_REMOVE.replaceAll("%id",
							context.getString(0)));
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
			final Set<Integer> prereq = parsePrerequisites(context.getArgs(), 1);
			qMan.addObjectivePrerequisites(profMan.getProfile(sender.getName()), context.getInt(0),
					prereq, context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().OBJ_PREREQ_ADD.replaceAll("%id", context.getString(0)));
		}
		
		@QCommandLabels({ "remove", "r" })
		@QCommand(
				section = "QMod",
				desc = "removes objective prerequisites",
				min = 1,
				usage = "<objective ID> <prerequisite1>...")
		public void remove(final QCommandContext context, final CommandSender sender) throws QuesterException {
			final Set<Integer> prereq = parsePrerequisites(context.getArgs(), 1);
			qMan.removeObjectivePrerequisites(profMan.getProfile(sender.getName()),
					context.getInt(0), prereq, context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().OBJ_PREREQ_REMOVE.replaceAll("%id",
							context.getString(0)));
		}
	}
}