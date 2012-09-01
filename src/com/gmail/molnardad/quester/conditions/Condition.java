package com.gmail.molnardad.quester.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public abstract class Condition implements ConfigurationSerializable {

	String desc = "";
	
	public abstract String getType();
	
	public String coloredDesc() {
		String des = "";
		if(!desc.isEmpty()) {
			des = "\n  - " + ChatColor.translateAlternateColorCodes('&', desc) + ChatColor.RESET;
		}
		return des;
	}
	
	public void addDescription(String msg) {
		this.desc += (" " + msg).trim();
	}
	
	public void removeDescription() {
		this.desc = "";
	}
	
	public abstract boolean isMet(Player player);
	public abstract String show();
	public abstract String toString();
	
	protected final void loadSuper(Map<String, Object> map) {
		String d = "";
		try{
			if(map.get("description") != null)
				d = (String) map.get("description");
		} catch (Exception e) {}
		desc = d;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(!desc.isEmpty())
			map.put("description", desc);
		
		return map;
	}
}
