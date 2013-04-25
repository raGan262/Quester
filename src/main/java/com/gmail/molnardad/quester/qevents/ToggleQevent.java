package com.gmail.molnardad.quester.qevents;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("TOGGLE")
public final class ToggleQevent extends Qevent {

	/**
	 * @uml.property  name="quest"
	 */
	private final int quest;
	
	public ToggleQevent(int qst) {
		this.quest = qst;
	}
	
	@Override
	public String info() {
		return String.valueOf(quest);
	}

	@Override
	protected void run(Player player, Quester plugin) {
		try {
			plugin.getQuestManager().toggleQuest(quest, plugin.getLanguageManager().getPlayerLang(player.getName()));
		} catch (QuesterException e) {
			Quester.log.info("Event failed to toggle quest. Reason: " + ChatColor.stripColor(e.getMessage()));
		}
	}

	@QCommand(
			min = 1,
			max = 1,
			usage = "<quest ID>")
	public static Qevent fromCommand(QCommandContext context) {
		return new ToggleQevent(context.getInt(0));
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
		
		return new ToggleQevent(qst);
	}
}
