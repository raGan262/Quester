package com.gmail.molnardad.quester.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.QCommandLabels;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.quests.QuestFlag;
import com.gmail.molnardad.quester.quests.QuestManager;

public class ModifierCommands {
	
	QuestManager qMan = null;
	
	public ModifierCommands(Quester plugin) {
		qMan = plugin.getQuestManager();
	}
	
	private QuestFlag[] getModifiers(String[] args) {
		Set<QuestFlag> modifiers = new HashSet<QuestFlag>();
		
		for(int i=0; i<args.length; i++) {
			QuestFlag flag = QuestFlag.getByName(args[i]);
			if(flag != null && flag != QuestFlag.ACTIVE) {
				modifiers.add(flag);
			}
		}
		
		return modifiers.toArray(new QuestFlag[0]);
	}
	
	@QCommandLabels({"add", "a"})
	@QCommand(
			section = "QMod",
			desc = "adds quest modifier",
			min = 1,
			usage = "<modifier1> ...")
	public void add(QCommandContext context, CommandSender sender) throws QuesterException {
		QuestFlag[] modArray = getModifiers(context.getArgs());
		if(modArray.length < 1) {
			sender.sendMessage(ChatColor.RED + context.getSenderLang().ERROR_MOD_UNKNOWN);
			sender.sendMessage(ChatColor.RED + context.getSenderLang().USAGE_MOD_AVAIL
					+ ChatColor.WHITE + QuestFlag.stringize(QuestFlag.values()));
			return;
		}
		qMan.addQuestFlag(sender.getName(), modArray, context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_MOD_ADDED);
	}
	
	@QCommandLabels({"remove", "r"})
	@QCommand(
			section = "QMod",
			desc = "sets quest modifier",
			min = 1,
			usage = "<modifier1> ...")
	public void set(QCommandContext context, CommandSender sender) throws QuesterException {
		QuestFlag[] modArray = getModifiers(context.getArgs());
		qMan.removeQuestFlag(sender.getName(), modArray, context.getSenderLang());
		sender.sendMessage(ChatColor.GREEN + context.getSenderLang().Q_MOD_REMOVED);
	}
}
