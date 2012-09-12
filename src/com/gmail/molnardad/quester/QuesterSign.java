package com.gmail.molnardad.quester;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

import com.gmail.molnardad.quester.utils.Util;

public class QuesterSign {

	private int holder = -1;
	private final Location location;
	
	public QuesterSign(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public int getHolderID() {
		return holder;
	}
	
	public void setHolderID(int ID) {
		holder = ID;
	} 
	
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("location", Util.serializeLocation(location));
		if(holder != -1)
			map.put("holder", holder);
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static QuesterSign deserialize(Map<String, Object> map) {
		Location loc;
		QuesterSign sign = null;
		
		try{
			loc = Util.deserializeLocation((Map<String, Object>) map.get("location"));
			sign = new QuesterSign(loc);
			
			if(map.get("holder") != null) {
				int qh = (Integer) map.get("holder");
				sign.setHolderID(qh);
			}
		} catch (Exception e) {
		}
			
		return sign;
	}
}
