package com.gmail.molnardad.quester.qevents;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.exceptions.QuesterException;

@SerializableAs("QuesterCancelQevent")
public final class CancelQevent extends Qevent {

	private final String TYPE = "CANCEL";
	
	public CancelQevent(int occ, int del){
		super(occ, del);
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
		return TYPE + ": ON-" + parseOccasion(occasion);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("occasion", occasion);
		map.put("delay", delay);
		
		return map;
	}
	
	public static CancelQevent deserialize(Map<String, Object> map) {
		int occ, del;
		
		occ = (Integer) map.get("occasion");
		del = (Integer) map.get("delay");
		
		return new CancelQevent(occ, del);
	}

	@Override
	public void run(Player player) {
		try {
			Quester.qMan.cancelQuest(player);
		} catch (QuesterException e) {
			Quester.log.info("Event failed to cancel " + player.getName() + "'s quest. Reason: " + ChatColor.stripColor(e.message()));
		}
	}
}
