package com.gmail.molnardad.quester.qevents;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Ql;

@QElement("QUEST")
public final class QuestQevent extends Qevent {
	
	private final int quest;
	
	public QuestQevent(final int qst) {
		quest = qst;
	}
	
	@Override
	public String info() {
		return String.valueOf(quest);
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		try {
			plugin.getProfileManager().startQuest(player, quest, ActionSource.eventSource(this),
					plugin.getLanguageManager().getPlayerLang(player.getName()));
		}
		catch (final QuesterException e) {
			Ql.warning("Event failed to give quest to " + player.getName() + ". Reason: "
					+ ChatColor.stripColor(e.getMessage()));
		}
	}
	
	@QCommand(min = 1, max = 1, usage = "<quest ID>")
	public static Qevent fromCommand(final QCommandContext context) throws QCommandException {
		final int id = context.getInt(0);
		if(id < 0) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_BAD_ID);
		}
		return new QuestQevent(id);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setInt("quest", quest);
	}
	
	protected static Qevent load(final StorageKey key) {
		final int qst = key.getInt("quest", -1);
		if(qst < 0) {
			return null;
		}
		
		return new QuestQevent(qst);
	}
}
