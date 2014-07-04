package me.ragan262.quester.commands;

import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.QCommandLabels;
import me.ragan262.quester.commandbase.QNestedCommand;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
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
	
	@QCommandLabels({ "info" })
	@QCommand(
			section = "Mod",
			desc = "shows detailed info about the quest",
			max = 1,
			usage = "[quest ID]",
			permission = QConfiguration.PERM_MODIFY)
	public void info(final QCommandContext context, final CommandSender sender) throws QuesterException {
		if(context.length() > 0) {
			messenger.showQuestInfo(sender, qMan.getQuest(context.getInt(0)));
		}
		else {
			messenger.showQuestInfo(sender, profMan.getProfile(sender.getName()).getSelected());
		}
	}
	
	@QCommandLabels({ "create", "c" })
	@QCommand(
			section = "Mod",
			desc = "creates a quest",
			min = 1,
			max = 1,
			usage = "<quest name>",
			permission = QConfiguration.PERM_MODIFY)
	public void create(final QCommandContext context, final CommandSender sender) throws QuesterException {
		final PlayerProfile prof = profMan.getProfile(sender.getName());
		final Quest quest = qMan.createQuest(prof, context.getString(0), context.getSenderLang());
		profMan.selectQuest(prof, quest);
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_CREATED"));
		Ql.verbose(sender.getName() + " created quest '" + context.getString(0) + "'.");
	}
	
	@QCommandLabels({ "remove", "r" })
	@QCommand(
			section = "Mod",
			desc = "removes the quest",
			min = 1,
			max = 1,
			usage = "<quest ID>",
			permission = QConfiguration.PERM_MODIFY)
	public void remove(final QCommandContext context, final CommandSender sender) throws QuesterException {
		final String name =
				qMan.removeQuest(profMan.getProfile(sender.getName()), context.getInt(0),
						context.getSenderLang()).getName();
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_REMOVED"));
		Ql.verbose(sender.getName() + " removed quest '" + name + "'.");
	}
	
	@QCommandLabels({ "name" })
	@QCommand(
			section = "QMod",
			desc = "renames the quest",
			min = 1,
			max = 1,
			usage = "<new name>",
			permission = QConfiguration.PERM_MODIFY)
	public void name(final QCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.changeQuestName(profMan.getProfile(sender.getName()), context.getString(0),
				context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().get("Q_RENAMED").replaceAll("%q", context.getString(0)));
	}
	
	@QCommandLabels({ "toggle" })
	@QCommand(
			section = "Mod",
			desc = "toggles the state of the quest",
			max = 1,
			usage = "[quest ID]",
			permission = QConfiguration.PERM_MODIFY)
	public void i(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
		boolean active;
		if(context.length() > 0) {
			active =
					qMan.toggleQuest(qMan.getQuest(context.getInt(0)), context.getSenderLang(),
							profMan);
		}
		else {
			active =
					qMan.toggleQuest(profMan.getProfile(sender.getName()).getSelected(),
							context.getSenderLang(), profMan);
		}
		if(active) {
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_ACTIVATED"));
		}
		else {
			sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_DEACTIVATED"));
		}
	}
	
	@QCommandLabels({ "select", "sel" })
	@QCommand(
			section = "Mod",
			desc = "selects the quest",
			min = 1,
			max = 1,
			usage = "<quest ID>",
			permission = QConfiguration.PERM_MODIFY)
	public void select(final QCommandContext context, final CommandSender sender) throws QuesterException {
		profMan.selectQuest(profMan.getProfile(sender.getName()), qMan.getQuest(context.getInt(0)));
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_SELECTED"));
	}
	
	// nested commands
	
	@QCommandLabels({ "desc" })
	@QCommand(
			section = "QMod",
			desc = "quest description manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(DescCommands.class)
	public void desc(final QCommandContext context, final CommandSender sender) throws QuesterException {}
	
	@QCommandLabels({ "location", "loc" })
	@QCommand(
			section = "QMod",
			desc = "quest location manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(LocationCommands.class)
	public void location(final QCommandContext context, final CommandSender sender) throws QCommandException {}
	
	@QCommandLabels({ "modifier", "mod" })
	@QCommand(
			section = "QMod",
			desc = "quest modifier manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(ModifierCommands.class)
	public void modifier(final QCommandContext context, final CommandSender sender) throws QCommandException {}
	
	@QCommandLabels({ "world" })
	@QCommand(
			section = "QMod",
			desc = "world restriction manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(WorldCommands.class)
	public void world(final QCommandContext context, final CommandSender sender) throws QCommandException {}
	
	@QCommandLabels({ "holder", "hol" })
	@QCommand(
			section = "Mod",
			desc = "quest holder manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(HolderCommands.class)
	public void holder(final QCommandContext context, final CommandSender sender) throws QCommandException {}
	
	@QCommandLabels({ "condition", "con" })
	@QCommand(
			section = "QMod",
			desc = "condition manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(ConditionCommands.class)
	public void condition(final QCommandContext context, final CommandSender sender) throws QCommandException {}
	
	@QCommandLabels({ "event", "evt" })
	@QCommand(
			section = "QMod",
			desc = "event manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(QeventCommands.class)
	public void event(final QCommandContext context, final CommandSender sender) throws QCommandException {}
	
	@QCommandLabels({ "objective", "obj" })
	@QCommand(
			section = "QMod",
			desc = "objective manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(ObjectiveCommands.class)
	public void objective(final QCommandContext context, final CommandSender sender) throws QCommandException {}
	
	@QCommandLabels({ "trigger", "trig" })
	@QCommand(
			section = "QMod",
			desc = "trigger manipulation",
			permission = QConfiguration.PERM_MODIFY)
	@QNestedCommand(TriggerCommands.class)
	public void trigger(final QCommandContext context, final CommandSender sender) throws QCommandException {}
}
