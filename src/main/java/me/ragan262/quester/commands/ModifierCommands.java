package me.ragan262.quester.commands;

import java.util.HashSet;
import java.util.Set;

import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.QCommandLabels;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.QuestFlag;
import me.ragan262.quester.quests.QuestManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ModifierCommands {
	
	final QuestManager qMan;
	final ProfileManager profMan;
	
	public ModifierCommands(final Quester plugin) {
		qMan = plugin.getQuestManager();
		profMan = plugin.getProfileManager();
	}
	
	private QuestFlag[] getModifiers(final String[] args) {
		final Set<QuestFlag> modifiers = new HashSet<QuestFlag>();
		
		for(int i = 0; i < args.length; i++) {
			final QuestFlag flag = QuestFlag.getByName(args[i]);
			if(flag != null && flag != QuestFlag.ACTIVE) {
				modifiers.add(flag);
			}
		}
		
		return modifiers.toArray(new QuestFlag[0]);
	}
	
	@QCommandLabels({ "add", "a" })
	@QCommand(section = "QMod", desc = "adds quest modifier", min = 1, usage = "<modifier1> ...")
	public void add(final QCommandContext context, final CommandSender sender) throws QuesterException {
		final QuestFlag[] modArray = getModifiers(context.getArgs());
		if(modArray.length < 1) {
			sender.sendMessage(ChatColor.RED + context.getSenderLang().get("ERROR_MOD_UNKNOWN"));
			sender.sendMessage(ChatColor.RED + context.getSenderLang().get("USAGE_MOD_AVAIL")
					+ ChatColor.WHITE + QuestFlag.stringize(QuestFlag.values()));
			return;
		}
		qMan.addQuestFlag(profMan.getSenderProfile(sender), modArray, context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_MOD_ADDED"));
	}
	
	@QCommandLabels({ "remove", "r" })
	@QCommand(section = "QMod", desc = "removes quest modifier", min = 1, usage = "<modifier1> ...")
	public void set(final QCommandContext context, final CommandSender sender) throws QuesterException {
		final QuestFlag[] modArray = getModifiers(context.getArgs());
		qMan.removeQuestFlag(profMan.getSenderProfile(sender), modArray, context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().get("Q_MOD_REMOVED"));
	}
}
