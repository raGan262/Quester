package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.managers.ProfileManager;
import com.gmail.molnardad.quester.managers.QuestHolderManager;

public class HolderCommands {
	
	QuestHolderManager holMan = null;
	ProfileManager profMan = null;
	
	public HolderCommands(Quester plugin) {
		holMan = plugin.getHolderManager();
		profMan = plugin.getProfileManager();
	}
	
	@QCommandLabels({"create", "c"})
	@QCommand(
			section = "QMod",
			desc = "creates a holder",
			min = 1,
			max = 1,
			usage = "<holder name>")
	public void set(QCommandContext context, CommandSender sender) throws QuesterException {
		int id = holMan.createHolder(context.getString(0));
		profMan.getProfile(sender.getName()).setHolderID(id);
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().HOL_CREATED);
	}
	
	@QCommandLabels({"delete", "d"})
	@QCommand(
			section = "QMod",
			desc = "deletes a holder",
			min = 1,
			max = 1,
			usage = "<holder ID>")
	public void delete(QCommandContext context, CommandSender sender) throws QuesterException {
		holMan.removeHolder(context.getInt(0));
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().HOL_REMOVED);
	}
	
	@QCommandLabels({"add", "a"})
	@QCommand(
			section = "QMod",
			desc = "adds quest to holder",
			min = 1,
			max = 1,
			usage = "<quest ID>")
	public void add(QCommandContext context, CommandSender sender) throws QuesterException {
		holMan.addHolderQuest(sender.getName(), context.getInt(0), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().HOL_Q_ADDED);
	}
	
	@QCommandLabels({"remove", "r"})
	@QCommand(
			section = "QMod",
			desc = "removes quest from holder",
			min = 1,
			max = 1,
			usage = "<quest ID>")
	public void remove(QCommandContext context, CommandSender sender) throws QuesterException {
		holMan.removeHolderQuest(sender.getName(), context.getInt(0), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().HOL_Q_REMOVED);
	}
	
	@QCommandLabels({"move", "m"})
	@QCommand(
			section = "QMod",
			desc = "moves quest in holder",
			min = 2,
			max = 2,
			usage = "<from> <to>")
	public void move(QCommandContext context, CommandSender sender) throws QuesterException {
		holMan.moveHolderQuest(sender.getName(), context.getInt(0), context.getInt(1), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().HOL_Q_MOVED);
	}
	
	@QCommandLabels({"list", "l"})
	@QCommand(
			section = "QMod",
			desc = "lists quest holders",
			max = 0)
	public void list(QCommandContext context, CommandSender sender) throws QuesterException {
		holMan.showHolderList(sender, context.getSenderLang());
	}
	
	@QCommandLabels({"info", "i"})
	@QCommand(
			section = "QMod",
			desc = "shows info about holder",
			min = 0,
			max = 1,
			usage = "[holder ID]")
	public void info(QCommandContext context, CommandSender sender) throws QuesterException {
		int id = -1;
		if(context.length() > 0) {
			id = context.getInt(0);
		}
		holMan.showHolderInfo(sender, id, context.getSenderLang());
	}
	
	@QCommandLabels({"select", "sel"})
	@QCommand(
			section = "QMod",
			desc = "selects holder",
			min = 1,
			max = 1,
			usage = "<holder ID>")
	public void select(QCommandContext context, CommandSender sender) throws QuesterException {
		profMan.selectHolder(sender.getName(), context.getInt(0));
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().HOL_SELECTED);
	}
}
