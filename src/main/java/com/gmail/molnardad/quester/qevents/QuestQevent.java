package com.gmail.molnardad.quester.qevents;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.QuesterException;

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
	protected void run(Player player) {
		try {
			QuestManager.getInstance().startQuest(player, quest, false);
		} catch (QuesterException e) {
			Quester.log.info("Event failed to give quest to " + player.getName() + ". Reason: " + ChatColor.stripColor(e.getMessage()));
		}
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("quest", quest);
	}
	
	public static QuestQevent deser(ConfigurationSection section) {
		int qst;
		
		if(section.isInt("quest"))
			qst = section.getInt("quest");
		else
			return null;
		
		return new QuestQevent(qst);
	}
}
