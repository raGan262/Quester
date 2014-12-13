package me.ragan262.quester.commands;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.ragan262.commandmanager.CommandHelp;
import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.annotations.CommandLabels;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.commandmanager.exceptions.PermissionException;
import me.ragan262.quester.ActionSource;
import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.lang.Messenger;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UserCommands {
	
	final QuestManager qMan;
	final ProfileManager profMan;
	final Quester plugin;
	final LanguageManager langMan;
	final Messenger messenger;
	
	public UserCommands(final Quester plugin) {
		qMan = plugin.getQuestManager();
		profMan = plugin.getProfileManager();
		langMan = plugin.getLanguageManager();
		messenger = plugin.getMessenger();
		this.plugin = plugin;
	}
	
	@CommandLabels({ "help" })
	@Command(section = "User", desc = "displays help", usage = "[arg1] [arg2]...")
	public void help(final QuesterCommandContext context, final CommandSender sender) {
		final Map<String, List<CommandHelp>> cmds = plugin.getCommandManager().getHelp(context.getArgs(), sender, context.hasFlag('d'));
		final QuesterLang lang = context.getSenderLang();
		final String[][] keys = {
				{ "User", Util.line(ChatColor.BLUE, lang.get("HELP_SECTION_USE"), ChatColor.GOLD) },
				{ "Mod", Util.line(ChatColor.BLUE, lang.get("HELP_SECTION_MODIFY"), ChatColor.GOLD) },
				{ "QMod", Util.line(ChatColor.DARK_GRAY, lang.get("HELP_SECTION_MODIFY_SELECTED")) },
				{
						"Hmod",
						Util.line(ChatColor.DARK_GRAY, lang.get("HELP_SECTION_MODIFY_HOLDER_SELECTED")) },
				{ "Admin", Util.line(ChatColor.BLUE, lang.get("HELP_SECTION_ADMIN")) } };
		StringBuilder sb;
		for(String[] key : keys) {
			if(cmds.containsKey(key[0])) {
				sb = new StringBuilder();
				sb.append(key[1]).append(ChatColor.RESET).append('\n');
				for(final CommandHelp ch : cmds.remove(key[0])) {
					sb.append(ch.getFormattedHelp()).append(ChatColor.RESET).append('\n');
				}
				sender.sendMessage(sb.toString());
			}
		}
		for(final String s : cmds.keySet()) {
			sb = new StringBuilder();
			sb.append(Util.line(ChatColor.BLUE, lang.get("HELP_SECTION_OTHER") + " (" + s + ")", ChatColor.GOLD)).append(ChatColor.RESET).append('\n');
			for(final CommandHelp ch : cmds.remove(s)) {
				sb.append(ch.getFormattedHelp()).append(ChatColor.RESET).append('\n');
			}
			sender.sendMessage(sb.toString());
		}
	}
	
	@CommandLabels({ "show" })
	@Command(
			section = "User",
			desc = "shows info about the quest",
			max = 1,
			usage = "\"[quest name]\"",
			permission = QConfiguration.PERM_USE_SHOW)
	public void show(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		Quest quest = null;
		if(context.length() > 0) {
			quest = qMan.getQuest(context.getString(0));
		}
		else {
			quest = profMan.getSenderProfile(sender).getSelected();
		}
		messenger.showQuest(sender, quest, context.getSenderLang());
	}
	
	@CommandLabels({ "list" })
	@Command(
			section = "User",
			desc = "displays quest list",
			max = 0,
			permission = QConfiguration.PERM_USE_LIST)
	public void list(final QuesterCommandContext context, final CommandSender sender) {
		if(Util.permCheck(sender, QConfiguration.PERM_MODIFY, false, null)) {
			messenger.showFullQuestList(sender, qMan, context.getSenderLang());
		}
		else {
			messenger.showQuestList(sender, qMan, profMan, context.getSenderLang());
		}
	}
	
	@CommandLabels({ "profile" })
	@Command(
			section = "User",
			desc = "shows player's profile",
			max = 1,
			usage = "[player]",
			permission = QConfiguration.PERM_USE_PROFILE)
	public void profile(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		if(Util.permCheck(sender, QConfiguration.PERM_ADMIN, false, null) && context.length() > 0) {
			final PlayerProfile prof = profMan.getProfileSafe(context.getString(0), context.getSenderLang());
			messenger.showProfile(sender, prof, context.getSenderLang());
		}
		else {
			messenger.showProfile(sender, profMan.getSenderProfile(sender), context.getSenderLang());
		}
	}
	
	@CommandLabels({ "start" })
	@Command(
			section = "User",
			desc = "starts the quest",
			max = 1,
			usage = "\"[quest name]\"",
			permission = QConfiguration.PERM_USE_START_RANDOM + "||"
					+ QConfiguration.PERM_USE_START_PICK)
	public void start(final QuesterCommandContext context, final CommandSender sender) throws QuesterException, CommandException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().get("MSG_ONLY_PLAYER"));
			return;
		}
		final ActionSource as = ActionSource.commandSource(sender);
		if(context.length() == 0) {
			if(Util.permCheck(sender, QConfiguration.PERM_USE_START_RANDOM, false, null)) {
				profMan.startRandomQuest(context.getPlayer(), as, context.getSenderLang());
			}
			else {
				throw new PermissionException(QConfiguration.PERM_USE_START_RANDOM);
			}
		}
		else if(Util.permCheck(sender, QConfiguration.PERM_USE_START_PICK, false, null)) {
			profMan.startQuest((Player)sender, qMan.getQuest(context.getString(0)), as, context.getSenderLang());
		}
		else {
			throw new PermissionException(QConfiguration.PERM_USE_START_PICK);
		}
	}
	
	@CommandLabels({ "done" })
	@Command(
			section = "User",
			desc = "completes current objective",
			max = 0,
			permission = QConfiguration.PERM_USE_DONE)
	public void done(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().get("MSG_ONLY_PLAYER"));
			return;
		}
		final ActionSource as = ActionSource.commandSource(sender);
		profMan.complete((Player)sender, as, context.getSenderLang());
	}
	
	@CommandLabels({ "cancel" })
	@Command(
			section = "User",
			desc = "drops current or selected quest",
			max = 1,
			usage = "[quest ID]",
			permission = QConfiguration.PERM_USE_CANCEL)
	public void cancel(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().get("MSG_ONLY_PLAYER"));
			return;
		}
		int index = -1;
		if(context.length() > 0) {
			index = context.getInt(0);
		}
		final ActionSource as = ActionSource.commandSource(sender);
		profMan.cancelQuest((Player)sender, index, as, context.getSenderLang());
	}
	
	@CommandLabels({ "switch" })
	@Command(
			section = "User",
			desc = "switches current quest",
			max = 1,
			min = 1,
			usage = "<index>",
			permission = QConfiguration.PERM_USE_SWITCH)
	public void switch0(final QuesterCommandContext context, final CommandSender sender) {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().get("MSG_ONLY_PLAYER"));
			return;
		}
		if(profMan.switchQuest(profMan.getSenderProfile(sender), context.getInt(0))) {
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_SWITCHED"));
		}
	}
	
	@CommandLabels({ "progress", "prog" })
	@Command(
			section = "User",
			desc = "shows quest progress",
			max = 1,
			usage = "[index]",
			permission = QConfiguration.PERM_USE_PROGRESS)
	public void progress(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		if(context.getPlayer() == null) {
			sender.sendMessage(context.getSenderLang().get("MSG_ONLY_PLAYER"));
			return;
		}
		int index = -1;
		if(context.length() > 0) {
			index = context.getInt(0);
		}
		messenger.showProgress((Player)sender, profMan.getSenderProfile(sender), index, context.getSenderLang());
	}
	
	@CommandLabels({ "quests" })
	@Command(
			section = "User",
			desc = "shows player's quests",
			max = 1,
			usage = "[player]",
			permission = QConfiguration.PERM_USE_QUESTS)
	public void quests(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		final PlayerProfile prof;
		if(Util.permCheck(sender, QConfiguration.PERM_ADMIN, false, null) && context.length() > 0) {
			prof = profMan.getProfileSafe(context.getString(0), context.getSenderLang());
		}
		else {
			prof = profMan.getSenderProfile(sender);
		}
		messenger.showTakenQuests(sender, prof, context.getSenderLang());
	}
	
	@CommandLabels({ "langs" })
	@Command(section = "User", desc = "shows available languages", max = 0)
	public void langs(final QuesterCommandContext context, final CommandSender sender) {
		final String playerLang = langMan.getLang(profMan.getSenderProfile(sender).getLanguage()).getName();
		final Set<String> langSet = langMan.getLangSet();
		String toAdd = null;
		for(final Iterator<String> i = langSet.iterator(); i.hasNext();) {
			final String l = i.next();
			if(l.equals(playerLang)) {
				i.remove();
				toAdd = ChatColor.GREEN + l + ChatColor.RESET;
				break;
			}
		}
		if(toAdd != null) {
			langSet.add(toAdd);
		}
		sender.sendMessage(ChatColor.GOLD + context.getSenderLang().get("AVAILABLE_LANGS") + ": "
				+ ChatColor.RESET + Util.implode(langSet.toArray(new String[langSet.size()]), ','));
	}
	
	@CommandLabels({ "lang" })
	@Command(
			section = "User",
			desc = "sets language",
			min = 1,
			max = 1,
			usage = "<language or RESET>")
	public void lang(final QuesterCommandContext context, final CommandSender sender) throws CommandException {
		final QuesterLang lang;
		String langName = context.getString(0);
		if(langName.equalsIgnoreCase("reset")) {
			langName = null;
		}
		if(profMan.setProfileLanguage(profMan.getSenderProfile(sender), langName)) {
			lang = langMan.getLang(langName);
			sender.sendMessage(ChatColor.GREEN + lang.get("MSG_LANG_SET"));
		}
		else {
			lang = context.getSenderLang();
			throw new CommandException(lang.get("ERROR_CMD_LANG_INVALID"));
		}
	}
}
