package com.gmail.molnardad.quester.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.commandbase.QNestedCommand;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.exceptions.ObjectiveException;
import com.gmail.molnardad.quester.exceptions.QuestException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.lang.LanguageManager;
import com.gmail.molnardad.quester.lang.QuesterLang;
import com.gmail.molnardad.quester.profiles.PlayerProfile;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.profiles.QuestProgress;
import com.gmail.molnardad.quester.quests.Quest;
import com.gmail.molnardad.quester.quests.QuestManager;
import com.gmail.molnardad.quester.utils.Util;

public class PlayerCommands {
	
	final ProfileManager profMan;
	
	public PlayerCommands(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
	private static PlayerProfile getProfileSafe(final ProfileManager pMan, final String playerName, final QuesterLang lang) throws QCommandException {
		if(!pMan.hasProfile(playerName)) {
			throw new QCommandException(lang.INFO_PROFILE_NOT_EXIST.replaceAll("%p", playerName));
		}
		return pMan.getProfile(playerName);
	}
	
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
		
		@QCommandLabels({ "list", "l" })
		@QCommand(section = "Admin", desc = "lists completed quests", max = 1, usage = "[player]")
		public void list(final QCommandContext context, final CommandSender sender) throws QCommandException {
			final PlayerProfile prof;
			if(context.length() > 0) {
				prof = getProfileSafe(profMan, context.getString(0), context.getSenderLang());
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
				prof = getProfileSafe(profMan, context.getString(0), context.getSenderLang());
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
					getProfileSafe(profMan, context.getString(0), context.getSenderLang());
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
					getProfileSafe(profMan, context.getString(0), context.getSenderLang());
			profMan.removeCompletedQuest(prof, context.getString(1));
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().PROF_COMP_REMOVED);
		}
	}
	
	public static class QuestCommands {
		
		final ProfileManager profMan;
		final LanguageManager langMan;
		final QuestManager qMan;
		
		public QuestCommands(final Quester plugin) {
			profMan = plugin.getProfileManager();
			langMan = plugin.getLanguageManager();
			qMan = plugin.getQuestManager();
		}
		
		@QCommandLabels({ "start" })
		@QCommand(
				section = "Admin",
				desc = "forces quest start",
				min = 2,
				max = 2,
				usage = "<player> <quest> (-e)")
		public void start(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
			final Player player = Bukkit.getPlayerExact(context.getString(0));
			final QuesterLang lang = context.getSenderLang();
			if(context.hasFlag('e')) {
				final PlayerProfile prof = getProfileSafe(profMan, context.getString(0), lang);
				final Quest quest = qMan.getQuest(context.getString(1));
				if(quest == null) {
					throw new QuestException(lang.ERROR_Q_NOT_EXIST);
				}
				profMan.assignQuest(prof, quest);
				if(player != null) {
					player.sendMessage(Quester.LABEL
							+ langMan.getPlayerLang(player.getName()).MSG_Q_STARTED.replaceAll(
									"%q", ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
				}
			}
			else {
				if(player == null) {
					throw new QCommandException(lang.ERROR_CMD_PLAYER_OFFLINE.replaceAll("%p",
							context.getString(0)));
				}
				profMan.startQuest(player, context.getString(1), ActionSource.adminSource(sender),
						lang);
			}
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().PROF_QUEST_STARTED);
		}
		
		@QCommandLabels({ "cancel" })
		@QCommand(
				section = "Admin",
				desc = "forces quest cancel",
				min = 2,
				max = 2,
				usage = "<player> <id> (-e)")
		public void cancel(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
			final QuesterLang lang = context.getSenderLang();
			final Player player = Bukkit.getPlayerExact(context.getString(0));
			final int index = context.getInt(1);
			if(context.hasFlag('e')) {
				if(index < 0) {
					throw new QuestException(lang.ERROR_Q_NOT_ASSIGNED);
				}
				final PlayerProfile prof = getProfileSafe(profMan, context.getString(0), lang);
				final String quest = prof.getQuest(index).getName();
				profMan.unassignQuest(prof, index);
				if(player != null) {
					player.sendMessage(Quester.LABEL
							+ langMan.getPlayerLang(player.getName()).MSG_Q_CANCELLED.replaceAll(
									"%q", ChatColor.GOLD + quest + ChatColor.BLUE));
				}
			}
			else {
				if(player == null) {
					throw new QCommandException(lang.ERROR_CMD_PLAYER_OFFLINE.replaceAll("%p",
							context.getString(0)));
				}
				profMan.cancelQuest(player, index, ActionSource.adminSource(sender), lang);
			}
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().PROF_QUEST_CANCELLED);
		}
	}
	
	public static class ReputationCommands {
		
		final ProfileManager profMan;
		
		public ReputationCommands(final Quester plugin) {
			profMan = plugin.getProfileManager();
		}
		
		@QCommandLabels({ "set", "s" })
		@QCommand(
				section = "Admin",
				desc = "sets quest points",
				min = 2,
				max = 2,
				usage = "<player> <points>")
		public void set(final QCommandContext context, final CommandSender sender) throws QCommandException {
			final PlayerProfile prof =
					getProfileSafe(profMan, context.getString(0), context.getSenderLang());
			final int points = context.getInt(1);
			profMan.addPoints(prof, points - prof.getPoints());
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().PROF_REPUTATION_SET);
		}
		
		@QCommandLabels({ "add", "a" })
		@QCommand(
				section = "Admin",
				desc = "adds quest points",
				min = 2,
				max = 2,
				usage = "<player> <points>")
		public void add(final QCommandContext context, final CommandSender sender) throws QCommandException {
			final PlayerProfile prof =
					getProfileSafe(profMan, context.getString(0), context.getSenderLang());
			final int points = context.getInt(1);
			profMan.addPoints(prof, points);
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().PROF_REPUTATION_ADDED);
		}
	}
	
	public static class ProgressCommands {
		
		final ProfileManager profMan;
		final QuestManager qMan;
		
		public ProgressCommands(final Quester plugin) {
			profMan = plugin.getProfileManager();
			qMan = plugin.getQuestManager();
		}
		
		@QCommandLabels({ "get", "g" })
		@QCommand(
				section = "Admin",
				desc = "gets quest progress",
				min = 1,
				max = 2,
				usage = "<player> [index]")
		public void get(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
			final PlayerProfile prof =
					getProfileSafe(profMan, context.getString(0), context.getSenderLang());
			final QuesterLang lang = context.getSenderLang();
			final int index;
			if(context.length() > 1) {
				index = context.getInt(1);
			}
			else {
				index = prof.getQuestProgressIndex();
			}
			final QuestProgress progress = prof.getProgress(index);
			
			if(progress == null) {
				throw new QuestException(lang.ERROR_Q_NOT_ASSIGNED);
			}
			
			final List<Objective> objectives = progress.getQuest().getObjectives();
			final int[] prog = progress.getProgress();
			
			sender.sendMessage(ChatColor.BLUE
					+ lang.PROF_PROGRESS.replaceAll("%p",
							ChatColor.GOLD + prof.getName() + ChatColor.BLUE).replaceAll("%q",
							ChatColor.GOLD + progress.getQuest().getName() + ChatColor.BLUE));
			
			for(int i = 0; i < objectives.size(); i++) {
				final Objective o = objectives.get(i);
				sender.sendMessage(String.format("[%d] %s: %d/%d", i, o.getType(), prog[i],
						o.getTargetAmount()));
			}
		}
		
		@QCommandLabels({ "set", "s" })
		@QCommand(
				section = "Admin",
				desc = "sets quest progress",
				min = 3,
				max = 4,
				usage = "<player> [index] <obj id> <progress>")
		public void set(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
			final PlayerProfile prof =
					getProfileSafe(profMan, context.getString(0), context.getSenderLang());
			final QuesterLang lang = context.getSenderLang();
			final int offset;
			final int index;
			if(context.length() == 3) {
				index = prof.getQuestProgressIndex();
				offset = 0;
			}
			else {
				index = context.getInt(1);
				offset = 1;
			}
			
			final QuestProgress progress = prof.getProgress(index);
			if(progress == null) {
				throw new QuestException(lang.ERROR_Q_NOT_ASSIGNED);
			}
			
			final int objectiveId = context.getInt(1 + offset);
			if(progress.getObjectiveStatus(objectiveId) == null) {
				throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
			}
			
			profMan.setProgress(prof, index, objectiveId, context.getInt(2 + offset));
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().PROF_PROGRESS_SET);
		}
	}
}
