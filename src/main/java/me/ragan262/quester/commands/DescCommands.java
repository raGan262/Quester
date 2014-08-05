package me.ragan262.quester.commands;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.annotations.CommandLabels;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.QuestManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DescCommands {
	
	final QuestManager qMan;
	final ProfileManager profMan;
	
	public DescCommands(final Quester plugin) {
		qMan = plugin.getQuestManager();
		profMan = plugin.getProfileManager();
	}
	
	@CommandLabels({ "set", "s" })
	@Command(
			section = "QMod",
			desc = "sets quest description",
			min = 0,
			max = 1,
			usage = "[new description]")
	public void set(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		String desc = "";
		if(context.length() > 0) {
			desc = context.getString(0);
		}
		qMan.setQuestDescription(profMan.getSenderProfile(sender), desc, context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_DESC_SET"));
	}
	
	@CommandLabels({ "add", "a" })
	@Command(
			section = "QMod",
			desc = "adds to quest description",
			min = 1,
			max = 1,
			usage = "<description to add>")
	public void add(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.addQuestDescription(profMan.getSenderProfile(sender), context.getString(0), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_DESC_SET"));
	}
}
