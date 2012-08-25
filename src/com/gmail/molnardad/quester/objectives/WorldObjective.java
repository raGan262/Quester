package com.gmail.molnardad.quester.objectives;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("QuesterWorldObjective")
public final class WorldObjective extends Objective {

	private final String TYPE = "WORLD";
	private final String worldName;
	
	public WorldObjective(String wName) {
		worldName = wName;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public String progress(int progress) {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%r", String.valueOf(1 - progress)).replaceAll("%t", String.valueOf(1));
		}
		return "Visit world '" + worldName + "'.";
	}
	
	@Override
	public String toString() {
		return TYPE+": "+worldName + coloredDesc() + stringQevents();
	}
	
	public boolean checkWorld(String wName) {
		return wName.equalsIgnoreCase(worldName);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		
		map.put("world", worldName);
		
		return map;
	}

	public static WorldObjective deserialize(Map<String, Object> map) {
		String world;
		try {
			world = (String) map.get("world");
			if(Bukkit.getWorld(world) == null)
				return null;
			
			WorldObjective obj = new WorldObjective(world);
			obj.loadSuper(map);
			return obj;
		} catch (Exception e) {
			return null;
		}
	}
}
