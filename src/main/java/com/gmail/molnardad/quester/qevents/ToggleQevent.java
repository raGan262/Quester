package com.gmail.molnardad.quester.qevents;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.exceptions.QuesterException;

public final class ToggleQevent extends Qevent {

	public static final String TYPE = "TOGGLE";
	private final int quest;
	
	public ToggleQevent(int occ, int del, int qst) {
		super(occ, del);
		this.quest = qst;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public int getOccasion() {
		return occasion;
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + quest + appendSuper();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		section.set("quest", quest);
	}
	
	public static ToggleQevent deser(int occ, int del, ConfigurationSection section) {
		int qst;
		
		if(section.isInt("quest"))
			qst = section.getInt("quest");
		else
			return null;
		
		return new ToggleQevent(occ, del, qst);
	}

	@Override
	void run(Player player) {
		try {
			QuestManager.getInstance().toggleQuest(quest);
		} catch (QuesterException e) {
			Quester.log.info("Event failed to toggle quest. Reason: " + ChatColor.stripColor(e.getMessage()));
		}
	}
}
