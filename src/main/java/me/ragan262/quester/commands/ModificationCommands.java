package me.ragan262.quester.commands;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.annotations.CommandLabels;
import me.ragan262.commandmanager.annotations.NestedCommand;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.Messenger;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.utils.Ql;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ModificationCommands {
	
	final QuestManager qMan;
	final ProfileManager profMan;
	final Messenger messenger;
	
	public ModificationCommands(final Quester plugin) {
		qMan = plugin.getQuestManager();
		profMan = plugin.getProfileManager();
		messenger = plugin.getMessenger();
	}
	
	@CommandLabels({ "info" })
	@Command(
			section = "Mod",
			desc = "shows detailed info about the quest",
			max = 1,
			usage = "[quest ID]",
			permission = QConfiguration.PERM_MODIFY)
	public void info(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		if(context.length() > 0) {
			messenger.showQuestInfo(sender, qMan.getQuest(context.getInt(0)), context.getSenderLang());
		}
		else {
			messenger.showQuestInfo(sender, profMan.getSenderProfile(sender).getSelected(), context.getSenderLang());
		}
	}
	
	@CommandLabels({ "create", "c" })
	@Command(
			section = "Mod",
			desc = "creates a quest",
			min = 1,
			max = 1,
			usage = "<quest name>",
			permission = QConfiguration.PERM_MODIFY)
	public void create(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		final PlayerProfile prof = profMan.getSenderProfile(sender);
		final Quest quest = qMan.createQuest(prof, context.getString(0), context.getSenderLang());
		profMan.selectQuest(prof, quest);
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_CREATED"));
		Ql.verbose(sender.getName() + " created quest '" + context.getString(0) + "'.");
	}
	
	@CommandLabels({ "remove", "r" })
	@Command(
			section = "Mod",
			desc = "removes the quest",
			min = 1,
			max = 1,
			usage = "<quest ID>",
			permission = QConfiguration.PERM_MODIFY)
	public void remove(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		final String name = qMan.removeQuest(profMan.getSenderProfile(sender), context.getInt(0), context.getSenderLang()).getName();
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_REMOVED"));
		Ql.verbose(sender.getName() + " removed quest '" + name + "'.");
	}
	
	@CommandLabels({ "name" })
	@Command(
			section = "QMod",
			desc = "renames the quest",
			min = 1,
			max = 1,
			usage = "<new name>",
			permission = QConfiguration.PERM_MODIFY)
	public void name(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.changeQuestName(profMan.getSenderProfile(sender), context.getString(0), context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().get("Q_RENAMED").replaceAll("%q", context.getString(0)));
	}
	
	@CommandLabels({ "toggle" })
	@Command(
			section = "Mod",
			desc = "toggles the state of the quest",
			max = 1,
			usage = "[quest ID]",
			permission = QConfiguration.PERM_MODIFY)
	public void i(final QuesterCommandContext context, final CommandSender sender) throws CommandException, QuesterException {
		boolean active;
		if(context.length() > 0) {
			active = qMan.toggleQuest(qMan.getQuest(context.getInt(0)), context.getSenderLang(), profMan);
		}
		else {
			active = qMan.toggleQuest(profMan.getSenderProfile(sender).getSelected(), context.getSenderLang(), profMan);
		}
		if(active) {
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_ACTIVATED"));
		}
		else {
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_DEACTIVATED"));
		}
	}
	
	@CommandLabels({ "select", "sel" })
	@Command(
			section = "Mod",
			desc = "selects the quest",
			min = 1,
			max = 1,
			usage = "<quest ID>",
			permission = QConfiguration.PERM_MODIFY)
	public void select(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {
		profMan.selectQuest(profMan.getSenderProfile(sender), qMan.getQuest(context.getInt(0)));
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_SELECTED"));
	}
	
	// nested commands
	
	@CommandLabels({ "desc" })
	@Command(
			section = "QMod",
			desc = "quest description manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@NestedCommand(DescCommands.class)
	public void desc(final QuesterCommandContext context, final CommandSender sender) throws QuesterException {}
	
	@CommandLabels({ "location", "loc" })
	@Command(
			section = "QMod",
			desc = "quest location manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@NestedCommand(LocationCommands.class)
	public void location(final QuesterCommandContext context, final CommandSender sender) throws CommandException {}
	
	@CommandLabels({ "modifier", "mod" })
	@Command(
			section = "QMod",
			desc = "quest modifier manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@NestedCommand(ModifierCommands.class)
	public void modifier(final QuesterCommandContext context, final CommandSender sender) throws CommandException {}
	
	@CommandLabels({ "world" })
	@Command(
			section = "QMod",
			desc = "world restriction manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@NestedCommand(WorldCommands.class)
	public void world(final QuesterCommandContext context, final CommandSender sender) throws CommandException {}
	
	@CommandLabels({ "holder", "hol" })
	@Command(
			section = "Mod",
			desc = "quest holder manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@NestedCommand(HolderCommands.class)
	public void holder(final QuesterCommandContext context, final CommandSender sender) throws CommandException {}
	
	@CommandLabels({ "condition", "con" })
	@Command(
			section = "QMod",
			desc = "condition manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@NestedCommand(ConditionCommands.class)
	public void condition(final QuesterCommandContext context, final CommandSender sender) throws CommandException {}
	
	@CommandLabels({ "event", "evt" })
	@Command(section = "QMod", desc = "event manipulation", permission = QConfiguration.PERM_MODIFY)
	@NestedCommand(QeventCommands.class)
	public void event(final QuesterCommandContext context, final CommandSender sender) throws CommandException {}
	
	@CommandLabels({ "objective", "obj" })
	@Command(
			section = "QMod",
			desc = "objective manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@NestedCommand(ObjectiveCommands.class)
	public void objective(final QuesterCommandContext context, final CommandSender sender) throws CommandException {}
	
	@CommandLabels({ "trigger", "trig" })
	@Command(
			section = "QMod",
			desc = "trigger manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@NestedCommand(TriggerCommands.class)
	public void trigger(final QuesterCommandContext context, final CommandSender sender) throws CommandException {}
}
