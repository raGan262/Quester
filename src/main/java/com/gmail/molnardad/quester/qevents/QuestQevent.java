package com.gmail.molnardad.quester.qevents;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("QUEST")
public final class QuestQevent extends Qevent {

	private final int quest;
	
	public QuestQevent(int qst) {
		this.quest = qst;
	}
	
	@Override
	public String info() {
		return String.valueOf(quest);
	}

	@Override
	protected void run(Player player, Quester plugin) {
		try {
			plugin.getProfileManager().startQuest(player, quest, ActionSource.eventSource(this), plugin.getLanguageManager().getPlayerLang(player.getName()));
		} catch (QuesterException e) {
			Quester.log.info("Event failed to give quest to " + player.getName() + ". Reason: " + ChatColor.stripColor(e.getMessage()));
		}
	}

	@QCommand(
			min = 1,
			max = 1,
			usage = "<quest ID>")
	public static Qevent fromCommand(QCommandContext context) {
		return new QuestQevent(context.getInt(0));
	}

	@Override
	protected void save(StorageKey key) {
		key.setInt("quest", quest);
	}
	
	protected static Qevent load(StorageKey key) {
		int qst;
		
		qst = key.getInt("quest", -1);
		if(qst < 0) {
			return null;
		}
		
		return new QuestQevent(qst);
	}
}
