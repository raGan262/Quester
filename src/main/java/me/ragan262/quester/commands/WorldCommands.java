package me.ragan262.quester.commands;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.annotations.CommandLabels;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.QuestManager;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class WorldCommands {
	
	final QuestManager qMan;
	final ProfileManager profMan;
	
	public WorldCommands(final Quester plugin) {
		qMan = plugin.getQuestManager();
		profMan = plugin.getProfileManager();
	}
	
	@CommandLabels({ "add", "a" })
	@Command(section = "QMod", desc = "adds quest world", min = 1, max = 1, usage = "{<world>}")
	public void set(final QuesterCommandContext context, final CommandSender sender) throws QuesterException, CommandException {
		World world = null;
		if(context.getString(0).equalsIgnoreCase(QConfiguration.worldLabelThis)) {
			if(context.getPlayer() != null) {
				world = context.getPlayer().getWorld();
			}
			else {
				throw new CommandException(context.getSenderLang().get("ERROR_CMD_WORLD_THIS")
						.replaceAll("%this", QConfiguration.worldLabelThis));
			}
		}
		else {
			world = sender.getServer().getWorld(context.getString(0));
		}
		if(world == null) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_WORLD_INVALID"));
		}
		qMan.addQuestWorld(profMan.getSenderProfile(sender), world.getName(),
				context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_WORLD_ADDED"));
	}
	
	@CommandLabels({ "remove", "r" })
	@Command(section = "QMod", desc = "removes quest world", min = 1, max = 1, usage = "{<world>}")
	public void remove(final QuesterCommandContext context, final CommandSender sender) throws QuesterException, CommandException {
		String worldName = context.getString(0);
		if(context.getString(0).equalsIgnoreCase(QConfiguration.worldLabelThis)) {
			if(context.getPlayer() != null) {
				worldName = context.getPlayer().getWorld().getName();
			}
			else {
				throw new CommandException(context.getSenderLang().get("ERROR_CMD_WORLD_THIS")
						.replaceAll("%this", QConfiguration.worldLabelThis));
			}
		}
		if(qMan.removeQuestWorld(profMan.getSenderProfile(sender), worldName,
				context.getSenderLang())) {
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_WORLD_REMOVED"));
		}
		else {
			throw new CommandException(context.getSenderLang().get("ERROR_WORLD_NOT_ASSIGNED"));
		}
	}
}
