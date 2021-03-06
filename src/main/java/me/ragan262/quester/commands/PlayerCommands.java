package me.ragan262.quester.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.annotations.CommandLabels;
import me.ragan262.commandmanager.annotations.NestedCommand;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.ActionSource;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.exceptions.ObjectiveException;
import me.ragan262.quester.exceptions.QuestException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.profiles.QuestProgress;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommands {
	
	final ProfileManager profMan;
	final LanguageManager langMan;
	
	public PlayerCommands(final Quester plugin) {
		profMan = plugin.getProfileManager();
		langMan = plugin.getLanguageManager();
	}
	
	@CommandLabels({ "completed", "compl" })
	@Command(section = "Admin", desc = "modification of completed quests")
	@NestedCommand(PlayerCommands.CompletedCommands.class)
	public void completed(final QuesterCommandContext context, final CommandSender sender) {}
	
	@CommandLabels({ "quest", "q" })
	@Command(section = "Admin", desc = "player quest manipulation")
	@NestedCommand(PlayerCommands.QuestCommands.class)
	public void quest(final QuesterCommandContext context, final CommandSender sender) {}
	
	@CommandLabels({ "reputation", "rep" })
	@Command(section = "Admin", desc = "reputation modification")
	@NestedCommand(PlayerCommands.ReputationCommands.class)
	public void reputation(final QuesterCommandContext context, final CommandSender sender) {}
	
	@CommandLabels({ "progress", "prog" })
	@Command(section = "Admin", desc = "progress modification")
	@NestedCommand(PlayerCommands.ProgressCommands.class)
	public void progress(final QuesterCommandContext context, final CommandSender sender) {}
	
	@CommandLabels({ "lang" })
	@Command(
			section = "Admin",
			desc = "gets or sets language",
			min = 1,
			max = 2,
			usage = "<player> [language]")
	public void lang(final QuesterCommandContext context, final CommandSender sender) throws CommandException, QuesterException {
		final PlayerProfile prof = profMan.getProfileSafe(context.getString(0), context.getSenderLang());
		if(context.length() > 1) {
			final QuesterLang lang;
			String langName = context.getString(1);
			if(langName.equalsIgnoreCase("reset")) {
				langName = null;
			}
			if(profMan.setProfileLanguage(prof, langName)) {
				lang = langMan.getLang(prof.getLanguage());
				sender.sendMessage(ChatColor.GREEN
						+ lang.get("PROF_LANGUAGE_SET").replaceAll("%p", prof.getName()));
			}
			else {
				lang = context.getSenderLang();
				throw new CommandException(lang.get("ERROR_CMD_LANG_INVALID"));
			}
		}
		else {
			sender.sendMessage(ChatColor.BLUE
					+ context.getSenderLang().get("PROF_LANGUAGE").replaceAll("%p", prof.getName())
					+ ": " + ChatColor.RESET + langMan.getLang(prof.getLanguage()).getName());
		}
	}
	
	public static class CompletedCommands {
		
		final ProfileManager profMan;
		
		public CompletedCommands(final Quester plugin) {
			profMan = plugin.getProfileManager();
		}
		
		@CommandLabels({ "list", "l" })
		@Command(section = "Admin", desc = "lists completed quests", max = 1, usage = "[player]")
		public void list(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			final PlayerProfile prof;
			if(context.length() > 0) {
				prof = profMan.getProfileSafe(context.getString(0), context.getSenderLang());
			}
			else {
				prof = profMan.getSenderProfile(sender);
			}
			sender.sendMessage(ChatColor.BLUE
					+ context.getSenderLang().get("INFO_PROFILE_COMPLETED").replaceAll("%p", prof.getName())
					+ ": \n" + ChatColor.WHITE + Util.implode(prof.getCompletedQuests(), ','));
		}
		
		@CommandLabels({ "find", "f" })
		@Command(
				section = "Admin",
				desc = "finds completed quests",
				min = 1,
				max = 2,
				usage = "[player] <partial quest name>")
		public void find(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			final PlayerProfile prof;
			final String pattern;
			if(context.length() > 1) {
				pattern = context.getString(1).toLowerCase();
				prof = profMan.getProfileSafe(context.getString(0), context.getSenderLang());
			}
			else {
				pattern = context.getString(0).toLowerCase();
				prof = profMan.getSenderProfile(sender);
			}
			sender.sendMessage(ChatColor.BLUE
					+ context.getSenderLang().get("INFO_PROFILE_COMPLETED").replaceAll("%p", prof.getName())
					+ ":");
			for(final String q : prof.getCompletedQuests()) {
				if(q.contains(pattern)) {
					final String completed = new SimpleDateFormat("d.M.yy HH:mm:ss z").format(new Date(prof.getCompletionTime(q) * 1000L));
					sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + completed
							+ ChatColor.BLUE + "] " + ChatColor.WHITE + q);
				}
			}
		}
		
		@CommandLabels({ "add", "a" })
		@Command(
				section = "Admin",
				desc = "adds to completed quests",
				min = 2,
				max = 3,
				usage = "<player> <quest> [time]")
		public void add(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			final PlayerProfile prof = profMan.getProfileSafe(context.getString(0), context.getSenderLang());
			long time = System.currentTimeMillis();
			if(context.length() > 2) {
				final int totake = context.getInt(2) * 1000;
				if(totake > 0) {
					time -= totake;
				}
			}
			profMan.addCompletedQuest(prof, context.getString(1), time);
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("PROF_COMP_ADDED"));
		}
		
		@CommandLabels({ "remove", "r" })
		@Command(
				section = "Admin",
				desc = "removes completed quest",
				min = 2,
				max = 2,
				usage = "<player> <quest>")
		public void remove(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			final PlayerProfile prof = profMan.getProfileSafe(context.getString(0), context.getSenderLang());
			profMan.removeCompletedQuest(prof, context.getString(1));
			if("ALL".equalsIgnoreCase(context.getString(1))) {
				for(final String q : prof.getCompletedQuests()) {
					profMan.removeCompletedQuest(prof, q);
				}
			}
			else {
				profMan.removeCompletedQuest(prof, context.getString(1));
			}
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("PROF_COMP_REMOVED"));
		}
	}
	
	public static class QuestCommands {
		
		final ProfileManager profMan;
		final LanguageManager langMan;
		final QuestManager qMan;
		final Quester plugin;
		
		public QuestCommands(final Quester plugin) {
			profMan = plugin.getProfileManager();
			langMan = plugin.getLanguageManager();
			qMan = plugin.getQuestManager();
			this.plugin = plugin;
		}
		
		@CommandLabels({ "start" })
		@Command(
				section = "Admin",
				desc = "forces quest start",
				min = 2,
				max = 2,
				usage = "<player> <quest> (-ef)")
		public void start(final QuesterCommandContext context, final CommandSender sender) throws CommandException, QuesterException {
			final Player player = Bukkit.getPlayerExact(context.getString(0));
			final QuesterLang lang = context.getSenderLang();
			final Quest quest = qMan.getQuest(context.getString(1));
			if(quest == null) {
				throw new QuestException(lang.get("ERROR_Q_NOT_EXIST"));
			}
			if(context.hasFlag('e')) {
				final PlayerProfile prof = profMan.getProfileSafe(context.getString(0), lang);
				profMan.assignQuest(prof, quest);
				if(player != null) {
					player.sendMessage(Quester.LABEL
							+ langMan.getLang(prof.getLanguage()).get("MSG_Q_STARTED").replaceAll("%q", ChatColor.GOLD
									+ quest.getName() + ChatColor.BLUE));
				}
			}
			else {
				if(player == null) {
					throw new CommandException(lang.get("ERROR_CMD_PLAYER_OFFLINE").replaceAll("%p", context.getString(0)));
				}
				final boolean disableAdminCheck = context.hasFlag('f');
				profMan.startQuest(player, quest, ActionSource.adminSource(sender), lang, disableAdminCheck);
			}
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("PROF_QUEST_STARTED"));
		}
		
		@CommandLabels({ "cancel" })
		@Command(
				section = "Admin",
				desc = "forces quest cancel",
				min = 2,
				max = 2,
				usage = "<player> <id> (-e)")
		public void cancel(final QuesterCommandContext context, final CommandSender sender) throws CommandException, QuesterException {
			final QuesterLang lang = context.getSenderLang();
			final Player player = Bukkit.getPlayerExact(context.getString(0));
			final int index = context.getInt(1);
			if(context.hasFlag('e')) {
				if(index < 0) {
					throw new QuestException(lang.get("ERROR_Q_NOT_ASSIGNED"));
				}
				final PlayerProfile prof = profMan.getProfileSafe(context.getString(0), lang);
				final String quest = prof.getQuest(index).getName();
				profMan.unassignQuest(prof, index);
				if(player != null) {
					player.sendMessage(Quester.LABEL
							+ langMan.getLang(prof.getLanguage()).get("MSG_Q_CANCELLED").replaceAll("%q", ChatColor.GOLD
									+ quest + ChatColor.BLUE));
				}
			}
			else {
				if(player == null) {
					throw new CommandException(lang.get("ERROR_CMD_PLAYER_OFFLINE").replaceAll("%p", context.getString(0)));
				}
				profMan.cancelQuest(player, index, ActionSource.adminSource(sender), lang);
			}
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("PROF_QUEST_CANCELLED"));
		}
		
		@CommandLabels({ "complete", "compl" })
		@Command(
				section = "Admin",
				desc = "forces quest complete",
				min = 2,
				max = 2,
				usage = "<player> <quest> (-ef)")
		public void complete(final QuesterCommandContext context, final CommandSender sender) throws CommandException, QuesterException {
			final QuesterLang lang = context.getSenderLang();
			final Quest quest = qMan.getQuest(context.getString(1));
			if(quest == null) {
				throw new QuestException(lang.get("ERROR_Q_NOT_EXIST"));
			}
			final PlayerProfile prof;
			final Player player;
			final boolean runEvents = context.hasFlag('e');
			if(runEvents) {
				player = Bukkit.getPlayerExact(context.getString(0));
				if(player == null) {
					throw new CommandException(lang.get("ERROR_CMD_PLAYER_OFFLINE").replaceAll("%p", context.getString(0)));
				}
				prof = profMan.getProfile(player);
			}
			else {
				player = null;
				prof = profMan.getProfileSafe(context.getString(0), lang);
			}
			if(context.hasFlag('f') && prof.isCompleted(quest.getName())) {
				throw new CommandException(lang.get("ERROR_PROF_Q_ALREADY_DONE"));
			}
			final int id = prof.getQuestProgressIndex(quest);
			if(id >= 0) {
				profMan.unassignQuest(prof, id);
			}
			profMan.addCompletedQuest(prof, context.getString(1), System.currentTimeMillis());
			
			if(runEvents) {
				for(final Qevent qevent : quest.getQevents()) {
					if(qevent.getOccasion() == -3) {
						qevent.execute(player);
					}
				}
			}
			
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("PROF_QUEST_COMPLETED"));
		}
	}
	
	public static class ReputationCommands {
		
		final ProfileManager profMan;
		
		public ReputationCommands(final Quester plugin) {
			profMan = plugin.getProfileManager();
		}
		
		@CommandLabels({ "set", "s" })
		@Command(
				section = "Admin",
				desc = "sets quest points",
				min = 2,
				max = 2,
				usage = "<player> <points>")
		public void set(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			final PlayerProfile prof = profMan.getProfileSafe(context.getString(0), context.getSenderLang());
			final int points = context.getInt(1);
			profMan.addPoints(prof, points - prof.getPoints());
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("PROF_REPUTATION_SET"));
		}
		
		@CommandLabels({ "add", "a" })
		@Command(
				section = "Admin",
				desc = "adds quest points",
				min = 2,
				max = 2,
				usage = "<player> <points>")
		public void add(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			final PlayerProfile prof = profMan.getProfileSafe(context.getString(0), context.getSenderLang());
			final int points = context.getInt(1);
			profMan.addPoints(prof, points);
			sender.sendMessage(ChatColor.GREEN
					+ context.getSenderLang().get("PROF_REPUTATION_ADDED"));
		}
	}
	
	public static class ProgressCommands {
		
		final ProfileManager profMan;
		final QuestManager qMan;
		
		public ProgressCommands(final Quester plugin) {
			profMan = plugin.getProfileManager();
			qMan = plugin.getQuestManager();
		}
		
		@CommandLabels({ "get", "g" })
		@Command(
				section = "Admin",
				desc = "gets quest progress",
				min = 1,
				max = 2,
				usage = "<player> [index]")
		public void get(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			final PlayerProfile prof = profMan.getProfileSafe(context.getString(0), context.getSenderLang());
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
				throw new QuestException(lang.get("ERROR_Q_NOT_ASSIGNED"));
			}
			
			final List<Objective> objectives = progress.getQuest().getObjectives();
			final List<Integer> prog = progress.getProgress();
			
			sender.sendMessage(ChatColor.BLUE
					+ lang.get("PROF_PROGRESS").replaceAll("%p", ChatColor.GOLD + prof.getName()
							+ ChatColor.BLUE).replaceAll("%q", ChatColor.GOLD
							+ progress.getQuest().getName() + ChatColor.BLUE));
			
			for(int i = 0; i < objectives.size(); i++) {
				final Objective o = objectives.get(i);
				sender.sendMessage(String.format("[%d] %s: %d/%d", i, o.getType(), prog.get(i), o.getTargetAmount()));
			}
		}
		
		@CommandLabels({ "set", "s" })
		@Command(
				section = "Admin",
				desc = "sets quest progress",
				min = 3,
				max = 4,
				usage = "<player> [index] <obj id> <progress>")
		public void set(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
			final PlayerProfile prof = profMan.getProfileSafe(context.getString(0), context.getSenderLang());
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
				throw new QuestException(lang.get("ERROR_Q_NOT_ASSIGNED"));
			}
			
			final int objectiveId = context.getInt(1 + offset);
			if(progress.getObjectiveStatus(objectiveId) == null) {
				throw new ObjectiveException(lang.get("ERROR_OBJ_NOT_EXIST"));
			}
			
			profMan.setProgress(prof, index, objectiveId, context.getInt(2 + offset));
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("PROF_PROGRESS_SET"));
		}
	}
}
