package me.ragan262.quester.commands;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.annotations.CommandLabels;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.utils.SerUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class LocationCommands {
	
	final QuestManager qMan;
	final ProfileManager profMan;
	
	public LocationCommands(final Quester plugin) {
		qMan = plugin.getQuestManager();
		profMan = plugin.getProfileManager();
	}
	
	@CommandLabels({ "set", "s" })
	@Command(
			section = "QMod",
			desc = "sets quest location",
			min = 2,
			max = 2,
			usage = "{<location>} <range>")
	public void set(final QuesterCommandContext context, final CommandSender sender) throws QuesterException, CommandException {
		try {
			final int range = context.getInt(1);
			if(range < 1) {
				throw new NumberFormatException();
			}
			qMan.setQuestLocation(profMan.getSenderProfile(sender), SerUtils.getLoc(sender, context.getString(0)), range, context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_LOC_SET"));
		}
		catch(final NumberFormatException e) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_RANGE_INVALID"));
		}
		catch(final IllegalArgumentException e) {
			throw new CommandException(e.getMessage());
		}
	}
	
	@CommandLabels({ "remove", "r" })
	@Command(section = "QMod", desc = "removes quest location", max = 0)
	public void remove(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.removeQuestLocation(profMan.getSenderProfile(sender), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_LOC_REMOVED"));
	}
}
