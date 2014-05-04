package me.ragan262.quester.commands;

import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.QCommandLabels;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.elements.ElementManager;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.exceptions.ElementException;
import me.ragan262.quester.exceptions.QeventException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.utils.SerUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QeventCommands {
	
	final QuestManager qMan;
	final ElementManager eMan;
	final ProfileManager profMan;
	final Quester plugin;
	
	public QeventCommands(final Quester plugin) {
		qMan = plugin.getQuestManager();
		eMan = plugin.getElementManager();
		profMan = plugin.getProfileManager();
		this.plugin = plugin;
	}
	
	private Qevent getQevent(final String type, final String occassion, final QCommandContext subContext, final QuesterLang lang) throws QeventException, QCommandException, QuesterException {
		int[] occasion;
		try {
			occasion = SerUtils.deserializeOccasion(occassion, lang);
		}
		catch (final IllegalArgumentException e) {
			throw new QCommandException(e.getMessage());
		}
		if(!eMan.isEvent(type)) {
			subContext.getSender().sendMessage(ChatColor.RED + lang.get("ERROR_EVT_NOT_EXIST"));
			subContext.getSender().sendMessage(
					ChatColor.RED + lang.get("EVT_LIST") + ": " + ChatColor.WHITE
							+ eMan.getEventList());
			throw new QeventException(lang.get("ERROR_EVT_NOT_EXIST"));
		}
		final Qevent evt = eMan.getEventFromCommand(type, subContext);
		if(evt != null) {
			evt.setOccasion(occasion[0], occasion[1]);
		}
		else {
			throw new ElementException(lang.get("ERROR_ELEMENT_FAIL"));
		}
		return evt;
	}
	
	@QCommandLabels({ "run" })
	@QCommand(
			section = "Admin",
			desc = "runs an event",
			min = 1,
			usage = "<event type> [args]",
			permission = QConfiguration.PERM_ADMIN)
	public void run(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
		if(!(sender instanceof Player)) {
			throw new QCommandException("This command requires player context.");
		}
		final QuesterLang lang = context.getSenderLang();
		final String type = context.getString(0);
		Qevent qevent;
		try {
			qevent = getQevent(type, "0", context.getSubContext(1), lang);
		}
		catch (final QeventException e) {
			return;
		}
		qevent.execute(context.getPlayer(), plugin);
	}
	
	@QCommandLabels({ "runas" })
	@QCommand(
			section = "Admin",
			desc = "runs an event as player",
			min = 3,
			max = 3,
			usage = "<player> <quest id> <event id>",
			permission = QConfiguration.PERM_ADMIN)
	public void runas(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
		final Player player = Bukkit.getPlayerExact(context.getString(0));
		final QuesterLang lang = context.getSenderLang();
		if(player == null) {
			throw new QCommandException(lang.get("ERROR_CMD_PLAYER_OFFLINE").replaceAll("%p",
					context.getString(0)));
		}
		
		final Qevent qevent;
		final int questId = context.getInt(1);
		final int eventId = context.getInt(2);
		try {
			qevent = qMan.getQuest(questId).getQevent(eventId);
		}
		catch (final Exception e) {
			throw new QeventException(lang.get("ERROR_EVT_NOT_EXIST"));
		}
		qevent.execute(context.getPlayer(), plugin);
	}
	
	@QCommandLabels({ "add", "a" })
	@QCommand(
			section = "QMod",
			desc = "adds an event",
			min = 2,
			usage = "{<occasion>} <event type> [args]")
	public void add(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
		final QuesterLang lang = context.getSenderLang();
		final String type = context.getString(1);
		Qevent qevent;
		try {
			qevent = getQevent(type, context.getString(0), context.getSubContext(2), lang);
		}
		catch (final QeventException e) {
			return;
		}
		qMan.addQuestQevent(profMan.getSenderProfile(sender), qevent, lang);
		sender.sendMessage(ChatColor.GREEN
				+ lang.get("EVT_ADD").replaceAll("%type", type.toUpperCase()));
	}
	
	@QCommandLabels({ "set", "s" })
	@QCommand(
			section = "QMod",
			desc = "sets an event",
			min = 3,
			usage = "<evt ID> {<occasion>} <evt type> [args]")
	public void set(final QCommandContext context, final CommandSender sender) throws QCommandException, QuesterException {
		final QuesterLang lang = context.getSenderLang();
		final String type = context.getString(2);
		final int qeventID = context.getInt(0);
		Qevent qevent;
		try {
			qevent = getQevent(type, context.getString(1), context.getSubContext(3), lang);
		}
		catch (final QeventException e) {
			return;
		}
		qMan.setQuestQevent(profMan.getSenderProfile(sender), qeventID, qevent, lang);
		sender.sendMessage(ChatColor.GREEN
				+ lang.get("EVT_SET").replaceAll("%type", type.toUpperCase()));
	}
	
	@QCommandLabels({ "remove", "r" })
	@QCommand(section = "QMod", desc = "removes event", min = 1, max = 1, usage = "<event ID>")
	public void remove(final QCommandContext context, final CommandSender sender) throws QuesterException {
		qMan.removeQuestQevent(profMan.getSenderProfile(sender), context.getInt(0),
				context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN
				+ context.getSenderLang().get("EVT_REMOVE").replaceAll("%id", context.getString(0)));
	}
	
	@QCommandLabels({ "list", "l" })
	@QCommand(section = "QMod", max = 0, desc = "event list")
	public void list(final QCommandContext context, final CommandSender sender) throws QuesterException {
		sender.sendMessage(ChatColor.RED + context.getSenderLang().get("EVT_LIST") + ": "
				+ ChatColor.WHITE + eMan.getEventList());
	}
}