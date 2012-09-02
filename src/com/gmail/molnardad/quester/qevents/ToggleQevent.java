package com.gmail.molnardad.quester.qevents;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.exceptions.QuesterException;

@SerializableAs("QuesterToggleQevent")
public final class ToggleQevent extends Qevent {

	private final String TYPE = "TOGGLE";
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
	
	public static ToggleQevent deserialize(Map<String, Object> map) {
		int occ, del, qst;
		
		qst = (Integer) map.get("quest");
		occ = (Integer) map.get("occasion");
		del = (Integer) map.get("delay");
		
		return new ToggleQevent(occ, del, qst);
	}

	@Override
	public void run(Player player) {
		try {
			Quester.qMan.toggleQuest(quest);
		} catch (QuesterException e) {
			Quester.log.info("Event failed to quest quest. Reason: " + ChatColor.stripColor(e.message()));
		}
	}
}
