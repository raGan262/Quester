package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.commandbase.QNestedCommand;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.QuestManager;

public class ModificationCommands {

	private QuestManager qMan = null;
	private ProfileManager profMan = null;
	
	public ModificationCommands(Quester plugin) {
		qMan = plugin.getQuestManager();
		profMan = plugin.getProfileManager();
	}
	
	@QCommandLabels({"info"})
	@QCommand(
			section = "Mod",
			desc = "shows detailed info about the quest",
			max = 1,
			usage = "[quest_ID]",
			permission = QConfiguration.PERM_MODIFY)
	public void info(QCommandContext context, CommandSender sender) throws QuesterException {
		if(context.length() > 0) {
			qMan.showQuestInfo(sender, context.getInt(0), context.getSenderLang());
		}
		else {
			qMan.showQuestInfo(sender, context.getSenderLang());
		}
	}
	
	@QCommandLabels({"create", "c"})
	@QCommand(
			section = "Mod",
			desc = "creates a quest",
			min = 1,
			max = 1,
			usage = "<quest name>",
			permission = QConfiguration.PERM_MODIFY)
	public void create(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.createQuest(sender.getName(), context.getString(0), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_CREATED);
		if(QConfiguration.verbose) {
			Quester.log.info(sender.getName() + " created quest '" + context.getString(0) + "'.");
		}
	}

	@QCommandLabels({"remove", "r"})
	@QCommand(
			section = "Mod",
			desc = "removes the quest",
			min = 1,
			max = 1,
			usage = "<quest ID>",
			permission = QConfiguration.PERM_MODIFY)
	public void remove(QCommandContext context, CommandSender sender) throws QuesterException {
		String name = qMan.removeQuest(sender.getName(), context.getInt(0), context.getSenderLang()).getName();
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_REMOVED);
		if(QConfiguration.verbose) {
			Quester.log.info(sender.getName() + " removed quest '" + name + "'.");
		}
	}

	@QCommandLabels({"name"})
	@QCommand(
			section = "QMod",
			desc = "renames the quest",
			min = 1,
			max = 1,
			usage = "<new name>",
			permission = QConfiguration.PERM_MODIFY)
	public void name(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.changeQuestName(sender.getName(), context.getString(0), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_RENAMED.replaceAll("%q", context.getString(0)));
	}

	@QCommandLabels({"toggle"})
	@QCommand(
			section = "Mod",
			desc = "toggles the state of the quest",
			max = 1,
			usage = "[quest ID]",
			permission = QConfiguration.PERM_MODIFY)
	public void i(QCommandContext context, CommandSender sender) throws QCommandException, QuesterException {
		boolean active;
		if(context.length() > 0) {
			active = qMan.toggleQuest(context.getInt(0), context.getSenderLang());
		}
		else {
			active = qMan.toggleQuest(sender, context.getSenderLang());
		}
		if(active){
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_ACTIVATED);
		} else {
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_DEACTIVATED);
		}
	}

	@QCommandLabels({"select", "sel"})
	@QCommand(
			section = "Mod",
			desc = "selects the quest",
			min = 1,
			max = 1,
			usage = "<quest ID>",
			permission = QConfiguration.PERM_MODIFY)
	public void select(QCommandContext context, CommandSender sender) throws QuesterException {
		profMan.selectQuest(sender.getName(), qMan.getQuest(context.getInt(0)));
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_SELECTED);
	}

	// nested commands
	
	@QCommandLabels({"desc"})
	@QCommand(
			section = "QMod",
			desc = "quest description manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(DescCommands.class)
	public void desc(QCommandContext context, CommandSender sender) throws QuesterException {
	}
	
	@QCommandLabels({"location", "loc"})
	@QCommand(
			section = "QMod",
			desc = "quest location manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(LocationCommands.class)
	public void location(QCommandContext context, CommandSender sender) throws QCommandException {
	}

	@QCommandLabels({"modifier", "mod"})
	@QCommand(
			section = "QMod",
			desc = "quest modifier manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(ModifierCommands.class)
	public void modifier(QCommandContext context, CommandSender sender) throws QCommandException {
	}

	@QCommandLabels({"world"})
	@QCommand(
			section = "QMod",
			desc = "world restriction manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(WorldCommands.class)
	public void world(QCommandContext context, CommandSender sender) throws QCommandException {
	}

	@QCommandLabels({"holder", "hol"})
	@QCommand(
			section = "Mod",
			desc = "quest holder manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(HolderCommands.class)
	public void holder(QCommandContext context, CommandSender sender) throws QCommandException {
	}

	@QCommandLabels({"condition", "con"})
	@QCommand(
			section = "QMod",
			desc = "condition manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(ConditionCommands.class)
	public void condition(QCommandContext context, CommandSender sender) throws QCommandException {
	}

	@QCommandLabels({"event", "evt"})
	@QCommand(
			section = "QMod",
			desc = "event manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(QeventCommands.class)
	public void event(QCommandContext context, CommandSender sender) throws QCommandException {
	}

	@QCommandLabels({"objective", "obj"})
	@QCommand(
			section = "QMod",
			desc = "objective manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(ObjectiveCommands.class)
	public void objective(QCommandContext context, CommandSender sender) throws QCommandException {
	}
}
