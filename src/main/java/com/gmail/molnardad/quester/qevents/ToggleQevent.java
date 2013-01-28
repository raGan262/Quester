package com.gmail.molnardad.quester.qevents;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.QuesterException;

@QElement("TOGGLE")
public final class ToggleQevent extends Qevent {

	private final int quest;
	
	public ToggleQevent(int qst) {
		this.quest = qst;
	}
	
	@Override
	public String info() {
		return String.valueOf(quest);
	}

	@Override
	protected void run(Player player) {
		try {
			QuestManager.getInstance().toggleQuest(quest);
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

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("quest", quest);
	}
	
	public static ToggleQevent deser(ConfigurationSection section) {
		int qst;
		
		if(section.isInt("quest"))
			qst = section.getInt("quest");
		else
			return null;
		
		return new ToggleQevent(qst);
	}
}
