package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.exceptions.QuesterException;

public class HolderCommands {
	
	QuestManager qMan = null;
	
	public HolderCommands(Quester plugin) {
		qMan = QuestManager.getInstance();
	}
	
	@QCommandLabels({"create", "c"})
	@QCommand(
			desc = "creates a holder",
			min = 1,
			max = 1,
			usage = "<holder name>")
	public void set(QCommandContext context, CommandSender sender) throws QuesterException {
		int id = qMan.createHolder(context.getString(0));
		qMan.getProfile(sender.getName()).setHolderID(id);
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().HOL_CREATED);
	}
	
	@QCommandLabels({"delete", "d"})
	@QCommand(
			desc = "deletes a holder",
			min = 1,
			max = 1,
			usage = "<holder ID>")
	public void delete(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.removeHolder(context.getInt(0));
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().HOL_REMOVED);
	}
	
	@QCommandLabels({"add", "a"})
	@QCommand(
			desc = "adds quest to holder",
			min = 1,
			max = 1,
			usage = "<quest ID>")
	public void add(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.addHolderQuest(sender.getName(), context.getInt(0));
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().HOL_Q_ADDED);
	}
	
	@QCommandLabels({"remove", "r"})
	@QCommand(
			desc = "removes quest from holder",
			min = 1,
			max = 1,
			usage = "<quest ID>")
	public void remove(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.removeHolderQuest(sender.getName(), context.getInt(0));
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().HOL_Q_REMOVED);
	}
	
	@QCommandLabels({"move", "m"})
	@QCommand(
			desc = "moves quest in holder",
			min = 2,
			max = 2,
			usage = "Quest in holder moved.")
	public void move(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.moveHolderQuest(sender.getName(), context.getInt(0), context.getInt(1));
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().HOL_Q_MOVED);
	}
	
	@QCommandLabels({"list", "l"})
	@QCommand(
			desc = "lists quest holders",
			max = 0)
	public void list(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.showHolderList(sender);
	}
	
	@QCommandLabels({"info", "i"})
	@QCommand(
			desc = "shows info about holder",
			min = 0,
			max = 1,
			usage = "[holder ID]")
	public void info(QCommandContext context, CommandSender sender) throws QuesterException {
		int id = -1;
		if(context.length() > 0) {
			id = context.getInt(0);
		}
		qMan.showHolderInfo(sender, id);
	}
	
	@QCommandLabels({"select", "sel"})
	@QCommand(
			desc = "selects holder",
			min = 1,
			max = 1,
			usage = "<holder ID>")
	public void select(QCommandContext context, CommandSender sender) throws QuesterException {
		qMan.selectHolder(sender.getName(), context.getInt(0));
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().HOL_SELECTED);
	}
}
