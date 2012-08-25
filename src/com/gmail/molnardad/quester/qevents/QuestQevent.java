package com.gmail.molnardad.quester.qevents;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.exceptions.QuesterException;

@SerializableAs("QuesterQuestQevent") // what a name
public final class QuestQevent extends Qevent {

	private final String TYPE = "QUEST";
	private final String quest;
	
	public QuestQevent(int occ, int del, String qst) {
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
		return TYPE + ": ON-" + parseOccasion(occasion) + "; QST: " + quest;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("quest", quest);
		map.put("occasion", occasion);
		map.put("delay", delay);
		
		return map;
	}
	
	public static QuestQevent deserialize(Map<String, Object> map) {
		String qst;
		int occ, del;
		
		qst = (String) map.get("quest");
		occ = (Integer) map.get("occasion");
		del = (Integer) map.get("delay");
		
		return new QuestQevent(occ, del, qst);
	}

	@Override
	public void run(Player player) {
		try {
			Quester.qMan.startQuest(player, quest, false);
		} catch (QuesterException e) {
			Quester.log.info("Event failed to give quest to " + player.getName() + ". Reason: " + ChatColor.stripColor(e.message()));
		}
	}
}
