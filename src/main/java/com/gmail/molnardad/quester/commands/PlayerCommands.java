package com.gmail.molnardad.quester.commands;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.commandbase.QNestedCommand;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.lang.QuesterLang;
import com.gmail.molnardad.quester.profiles.PlayerProfile;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.utils.Util;

public class PlayerCommands {
	
	final ProfileManager profMan;
	
	public PlayerCommands(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
	//	TODO: PLAYER COMANDS
	//	==============
	//	COMPLETED LIST/FIND/ADD/REMOVE - DONE
	//	QUEST START/CANCEL
	//	REPUTATION SET/ADD
	//	PROGRESS GET/SET
	
	@QCommandLabels({ "completed", "compl" })
	@QCommand(section = "Admin", desc = "modification of completed quests")
	@QNestedCommand(PlayerCommands.CompletedCommands.class)
	public void completed(final QCommandContext context, final CommandSender sender) {
	}
	
	@QCommandLabels({ "quest" })
	@QCommand(section = "Admin", desc = "player quest manipulation")
	@QNestedCommand(PlayerCommands.QuestCommands.class)
	public void quest(final QCommandContext context, final CommandSender sender) {
	}
	
	@QCommandLabels({ "reputation", "rep" })
	@QCommand(section = "Admin", desc = "reputation modification")
	@QNestedCommand(PlayerCommands.ReputationCommands.class)
	public void reputation(final QCommandContext context, final CommandSender sender) {
	}
	
	@QCommandLabels({ "progress", "prog" })
	@QCommand(section = "Admin", desc = "progress modification")
	@QNestedCommand(PlayerCommands.ProgressCommands.class)
	public void progress(final QCommandContext context, final CommandSender sender) {
	}
	
	public static class CompletedCommands {
		
		final ProfileManager profMan;
		
		public CompletedCommands(final Quester plugin) {
			profMan = plugin.getProfileManager();
		}
		
		private PlayerProfile getProfileSafe(final String playerName, final QuesterLang lang) throws QCommandException {
			if(!profMan.hasProfile(playerName)) {
				throw new QCommandException(
						lang.INFO_PROFILE_NOT_EXIST.replaceAll("%p", playerName));
			}
			return profMan.getProfile(playerName);
		}
		
		@QCommandLabels({ "list", "l" })
		@QCommand(section = "Admin", desc = "lists completed quests", max = 1, usage = "[player]")
		public void list(final QCommandContext context, final CommandSender sender) throws QCommandException {
			final PlayerProfile prof;
			if(context.length() > 0) {
				prof = getProfileSafe(context.getString(0), context.getSenderLang());
			}
			else {
				prof = profMan.getProfile(sender.getName());
			}
			sender.sendMessage(ChatColor.BLUE
					+ context.getSenderLang().INFO_PROFILE_COMPLETED.replaceAll("%p",
							prof.getName()) + ": \n" + ChatColor.WHITE
					+ Util.implode(prof.getCompletedQuests(), ','));
		}
		
		@QCommandLabels({ "find", "f" })
		@QCommand(
				section = "Admin",
				desc = "finds completed quests",
				min = 1,
				max = 2,
				usage = "[player] <partial quest name>")
		public void find(final QCommandContext context, final CommandSender sender) throws QCommandException {
			final PlayerProfile prof;
			final String pattern;
			if(context.length() > 1) {
				pattern = context.getString(1).toLowerCase();
				prof = getProfileSafe(context.getString(0), context.getSenderLang());
			}
			else {
				pattern = context.getString(0).toLowerCase();
				prof = profMan.getProfile(sender.getName());
			}
			sender.sendMessage(ChatColor.BLUE
					+ context.getSenderLang().INFO_PROFILE_COMPLETED.replaceAll("%p",
							prof.getName()) + ":");
			for(final String q : prof.getCompletedQuests()) {
				if(q.contains(pattern)) {
					final String completed =
							new SimpleDateFormat("d.M.yy HH:mm:ss z").format(new Date(prof
									.getCompletionTime(q) * 1000L));
					sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + completed
							+ ChatColor.BLUE + "] " + ChatColor.WHITE + q);
				}
			}
		}
		
		@QCommandLabels({ "add", "a" })
		@QCommand(
				section = "Admin",
				desc = "adds to completed quests",
				min = 2,
				max = 3,
				usage = "<player> <quest> [time]")
		public void add(final QCommandContext context, final CommandSender sender) throws QCommandException {
			final PlayerProfile prof =
					getProfileSafe(context.getString(0), context.getSenderLang());
			long time = System.currentTimeMillis();
			if(context.length() > 2) {
				final int totake = context.getInt(2) * 1000;
				if(totake > 0) {
					time -= totake;
				}
			}
			profMan.addCompletedQuest(prof, context.getString(1), time);
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().PROF_COMP_ADDED);
		}
		
		@QCommandLabels({ "remove", "r" })
		@QCommand(
				section = "Admin",
				desc = "removes completed quest",
				min = 2,
				max = 2,
				usage = "<player> <quest>")
		public void remove(final QCommandContext context, final CommandSender sender) throws QCommandException {
			final PlayerProfile prof =
					getProfileSafe(context.getString(0), context.getSenderLang());
			profMan.removeCompletedQuest(prof, context.getString(1));
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().PROF_COMP_REMOVED);
		}
	}
	
	public static class QuestCommands {
		
		final ProfileManager profMan;
		
		public QuestCommands(final Quester plugin) {
			profMan = plugin.getProfileManager();
		}
	}
	
	public static class ReputationCommands {
		
		final ProfileManager profMan;
		
		public ReputationCommands(final Quester plugin) {
			profMan = plugin.getProfileManager();
		}
	}
	
	public static class ProgressCommands {
		
		final ProfileManager profMan;
		
		public ProgressCommands(final Quester plugin) {
			profMan = plugin.getProfileManager();
		}
	}
}
