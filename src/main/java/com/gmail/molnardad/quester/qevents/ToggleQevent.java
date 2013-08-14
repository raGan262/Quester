package com.gmail.molnardad.quester.qevents;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.QuestException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.lang.LanguageManager;
import com.gmail.molnardad.quester.quests.Quest;
import com.gmail.molnardad.quester.quests.QuestManager;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Ql;

@QElement("TOGGLE")
public final class ToggleQevent extends Qevent {
	
	private final String quest;
	
	public ToggleQevent(final String qst) {
		quest = qst;
	}
	
	@Override
	public String info() {
		return quest;
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		final QuestManager qm = plugin.getQuestManager();
		String warning = null;
		Quest q = null;
		try {
			q = qm.getQuest(quest);
		}
		catch (final Exception ignore) {}
		try {
			if(q == null) {
				try {
					q = qm.getQuest(Integer.valueOf(quest));
					warning = "Deprecated usage of Quest ID in TOGGLE event detected.";
				}
				catch (final NumberFormatException e) {
					throw new QuestException(LanguageManager.defaultLang.get("ERROR_Q_NOT_EXIST"));
				}
			}
			qm.toggleQuest(q, LanguageManager.defaultLang);
			if(warning != null) {
				Ql.warning(warning);
			}
		}
		catch (final QuesterException e) {
			Ql.warning("Event failed to toggle quest. Reason: "
					+ ChatColor.stripColor(e.getMessage()));
		}
	}
	
	@QCommand(min = 1, max = 1, usage = "<quest name>")
	public static Qevent fromCommand(final QCommandContext context) throws QCommandException {
		final Quester plugin = (Quester) Bukkit.getPluginManager().getPlugin("Quester");
		
		if(plugin.getQuestManager().getQuest(context.getString(0)) == null) {
			
			context.getSender().sendMessage(
					ChatColor.YELLOW + context.getSenderLang().get("ERROR_Q_NOT_EXIST"));
		}
		return new ToggleQevent(context.getString(0));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("quest", quest);
	}
	
	protected static Qevent load(final StorageKey key) {
		final String qst = key.getString("quest");
		if(qst == null) {
			return null;
		}
		
		return new ToggleQevent(qst);
	}
}
