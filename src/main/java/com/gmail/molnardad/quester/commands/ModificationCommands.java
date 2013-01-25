package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.DataManager;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.commandbase.QNestedCommand;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.exceptions.QuesterException;

public class ModificationCommands {

	private QuestManager qMan = null;
	
	public ModificationCommands(Quester plugin) {
		qMan = QuestManager.getInstance();
	}
	
	@QCommandLabels({"info"})
	@QCommand(
			desc = "shows detailed info about the quest",
			max = 1,
			usage = "[quest_ID]",
			permission = DataManager.PERM_MODIFY)
	public void info(QCommandContext context, CommandSender sender) throws QuesterException {
		if(context.length() > 0) {
			qMan.showQuestInfo(sender, context.getInt(0));
		}
		else {
			qMan.showQuestInfo(sender);
		}
	}
	
	@QCommandLabels({"create", "c"})
	@QCommand(
			desc = "creates a quest",
			min = 1,
			max = 1,
			usage = "<quest name>",
			permission = DataManager.PERM_MODIFY)
	public void create(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.createQuest(sender.getName(), context.getString(0));
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_CREATED);
		if(DataManager.getInstance().verbose) {
			Quester.log.info(sender.getName() + " created quest '" + context.getString(0) + "'.");
		}
	}

	@QCommandLabels({"remove", "r"})
	@QCommand(
			desc = "removes the quest",
			min = 1,
			max = 1,
			usage = "<quest ID>",
			permission = DataManager.PERM_MODIFY)
	public void remove(QCommandContext context, CommandSender sender) throws QuesterException {
		try {
			String name = qMan.removeQuest(sender.getName(), context.getInt(0)).getName();
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_REMOVED);
			if(DataManager.getInstance().verbose) {
				Quester.log.info(sender.getName() + " removed quest '" + name + "'.");
			}
		}
		catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + context.getSenderLang().ERROR_CMD_BAD_ID);
		}
	}

	@QCommandLabels({"select", "sel"})
	@QCommand(
			desc = "selects the quest",
			min = 1,
			max = 1,
			usage = "<quest ID>",
			permission = DataManager.PERM_MODIFY)
	public void select(QCommandContext context, CommandSender sender) throws QuesterException {
		try {
			qMan.selectQuest(sender.getName(), context.getInt(0));
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_SELECTED);
		}
		catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + context.getSenderLang().ERROR_CMD_BAD_ID);
		}
	}

	@QCommandLabels({"name"})
	@QCommand(
			desc = "renames the quest",
			min = 1,
			max = 1,
			usage = "<new name>",
			permission = DataManager.PERM_MODIFY)
	public void name(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.changeQuestName(sender.getName(), context.getString(0));
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_RENAMED.replaceAll("%q", context.getString(0)));
	}	

	@QCommandLabels({"desc"})
	@QCommand(
			desc = "quest description manipulation",
			permission = DataManager.PERM_MODIFY)
	@QNestedCommand(DescCommands.class)
	public void desc(QCommandContext context, CommandSender sender) throws QuesterException {
	}
	
	@QCommandLabels({"location", "loc"})
	@QCommand(
			desc = "quest location manipulation",
			permission = DataManager.PERM_MODIFY)
	@QNestedCommand(LocationCommands.class)
	public void location(QCommandContext context, CommandSender sender) throws QCommandException {
	}

	@QCommandLabels({"modifier", "mod"})
	@QCommand(
			desc = "quest modifier manipulation",
			permission = DataManager.PERM_MODIFY)
	@QNestedCommand(ModifierCommands.class)
	public void modifier(QCommandContext context, CommandSender sender) throws QCommandException {
	}

	@QCommandLabels({""})
	@QCommand(
			desc = "",
			min = 0,
			max = 1,
			usage = "",
			permission = DataManager.PERM_MODIFY)
	public void h(QCommandContext context, CommandSender sender) throws QCommandException {
		
	}
}
