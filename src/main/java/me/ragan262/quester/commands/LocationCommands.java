package me.ragan262.quester.commands;

import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.QCommandLabels;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
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
	
	@QCommandLabels({ "set", "s" })
	@QCommand(
			section = "QMod",
			desc = "sets quest location",
			min = 2,
			max = 2,
			usage = "{<location>} <range>")
	public void set(final QCommandContext context, final CommandSender sender) throws QuesterException, QCommandException {
		try {
			final int range = context.getInt(1);
			if(range < 1) {
				throw new NumberFormatException();
			}
			qMan.setQuestLocation(profMan.getSenderProfile(sender),
					SerUtils.getLoc(sender, context.getString(0)), range, context.getSenderLang());
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_LOC_SET"));
		}
		catch (final NumberFormatException e) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_RANGE_INVALID"));
		}
		catch (final IllegalArgumentException e) {
			throw new QCommandException(e.getMessage());
		}
	}
	
	@QCommandLabels({ "remove", "r" })
	@QCommand(section = "QMod", desc = "removes quest location", max = 0)
	public void remove(final QCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.removeQuestLocation(profMan.getSenderProfile(sender), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_LOC_REMOVED"));
	}
}
