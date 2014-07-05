package me.ragan262.quester.commands;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.annotations.CommandLabels;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.exceptions.HolderException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.holder.QuestHolder;
import me.ragan262.quester.holder.QuestHolderManager;
import me.ragan262.quester.lang.Messenger;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.QuestManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HolderCommands {
	
	final QuestHolderManager holMan;
	final ProfileManager profMan;
	final QuestManager qMan;
	final Messenger messenger;
	
	public HolderCommands(final Quester plugin) {
		holMan = plugin.getHolderManager();
		profMan = plugin.getProfileManager();
		qMan = plugin.getQuestManager();
		messenger = plugin.getMessenger();
	}
	
	@CommandLabels({ "create", "c" })
	@Command(section = "Mod", desc = "creates a holder", min = 1, max = 1, usage = "<holder name>")
	public void set(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		final int id = holMan.createHolder(context.getString(0));
		profMan.selectHolder(profMan.getSenderProfile(sender), id);
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("HOL_CREATED"));
	}
	
	@CommandLabels({ "delete", "d" })
	@Command(section = "Mod", desc = "deletes a holder", min = 1, max = 1, usage = "<holder ID>")
	public void delete(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		holMan.removeHolder(context.getInt(0));
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("HOL_REMOVED"));
	}
	
	@CommandLabels({ "add", "a" })
	@Command(
			section = "HMod",
			desc = "adds quest to holder",
			min = 1,
			max = 1,
			usage = "<quest ID>")
	public void add(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		holMan.addHolderQuest(profMan.getSenderProfile(sender), context.getInt(0),
				context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("HOL_Q_ADDED"));
	}
	
	@CommandLabels({ "remove", "r" })
	@Command(
			section = "HMod",
			desc = "removes quest from holder",
			min = 1,
			max = 1,
			usage = "<quest ID>")
	public void remove(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		holMan.removeHolderQuest(profMan.getSenderProfile(sender), context.getInt(0),
				context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("HOL_Q_REMOVED"));
	}
	
	@CommandLabels({ "move", "m" })
	@Command(
			section = "HMod",
			desc = "moves quest in holder",
			min = 2,
			max = 2,
			usage = "<from> <to>")
	public void move(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		holMan.moveHolderQuest(profMan.getSenderProfile(sender), context.getInt(0),
				context.getInt(1), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("HOL_Q_MOVED"));
	}
	
	@CommandLabels({ "list", "l" })
	@Command(section = "Mod", desc = "lists quest holders", max = 0)
	public void list(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		messenger.showHolderList(sender, holMan, context.getSenderLang());
	}
	
	@CommandLabels({ "info", "i" })
	@Command(
			section = "Mod",
			desc = "shows info about holder",
			min = 0,
			max = 1,
			usage = "[holder ID]")
	public void info(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		int id;;
		if(context.length() > 0) {
			id = context.getInt(0);
		}
		else {
			id = profMan.getSenderProfile(sender).getHolderID();
		}
		final QuestHolder qh = holMan.getHolder(id);
		if(qh == null) {
			if(id < 0) {
				throw new HolderException(context.getSenderLang().get("ERROR_HOL_NOT_SELECTED"));
			}
			else {
				throw new HolderException(context.getSenderLang().get("ERROR_HOL_NOT_EXIST"));
			}
		}
		sender.sendMessage(ChatColor.GOLD + "Holder ID: " + ChatColor.RESET + id);
		messenger.showHolderQuestsModify(qh, sender, qMan);
	}
	
	@CommandLabels({ "select", "sel" })
	@Command(section = "Mod", desc = "selects holder", min = 1, max = 1, usage = "<holder ID>")
	public void select(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		profMan.selectHolder(profMan.getSenderProfile(sender), context.getInt(0));
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("HOL_SELECTED"));
	}
}
