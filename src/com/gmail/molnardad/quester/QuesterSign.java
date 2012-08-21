package com.gmail.molnardad.quester;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

import com.gmail.molnardad.quester.utils.Util;

public class QuesterSign {

	private final QuestHolder holder;
	private final Location location;
	
	public QuesterSign(Location location, QuestHolder holder) {
		this.location = location;
		this.holder = holder;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public QuestHolder getHolder() {
		return holder;
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("location", Util.serializeLocation(location));
		map.put("quests", holder.serialize());
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static QuesterSign deserialize(Map<String, Object> map) {
		Location loc;
		QuestHolder qh;
		QuesterSign sign = null;
		
		try{
			loc = Util.deserializeLocation((Map<String, Object>) map.get("location"));
			qh = QuestHolder.deserialize((String) map.get("quests"));
			
			sign = new QuesterSign(loc, qh);
		} catch (Exception e) {
		}
			
		return sign;
	}
}
