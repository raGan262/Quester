package com.gmail.molnardad.quester.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.DataManager;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.exceptions.QuesterException;

public class WorldCommands {
	
	QuestManager qMan = null;
	DataManager dtMan = null;
	
	public WorldCommands(Quester plugin) {
		qMan = plugin.getQuestManager();
		dtMan = DataManager.getInstance();
	}
	
	@QCommandLabels({"add", "a"})
	@QCommand(
			desc = "adds quest world",
			min = 1,
			max = 1,
			usage = "{<world>}")
	public void set(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		World world = null;
		if(context.getString(0).equalsIgnoreCase(dtMan.worldLabelThis)) {
			if(context.getPlayer() != null) {
				world = context.getPlayer().getWorld();
			} else {
				throw new QCommandException(context.getSenderLang()
						.ERROR_CMD_WORLD_THIS.replaceAll("%this", dtMan.worldLabelThis));
			}
		} else {
			world = sender.getServer().getWorld(context.getString(0));
		}
		if(world == null) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_WORLD_INVALID);
		}
		qMan.addQuestWorld(sender.getName(), world.getName());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_WORLD_ADDED);
	}
	
	@QCommandLabels({"remove", "r"})
	@QCommand(
			desc = "removes quest world",
			min = 1,
			max = 1,
			usage = "{<world>}")
	public void remove(QCommandContext context, CommandSender sender) throws QuesterException, QCommandException {
		String worldName = context.getString(0);
		if(context.getString(0).equalsIgnoreCase(dtMan.worldLabelThis)) {
			if(context.getPlayer() != null) {
				worldName = context.getPlayer().getWorld().getName();
			} else {
				throw new QCommandException(context.getSenderLang()
						.ERROR_CMD_WORLD_THIS.replaceAll("%this", dtMan.worldLabelThis));
			}
		}
		if(qMan.removeQuestWorld(sender.getName(), worldName)) {
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_WORLD_REMOVED);
		}
		else {
			throw new QCommandException(context.getSenderLang().ERROR_WORLD_NOT_ASSIGNED);
		}
	}
}
