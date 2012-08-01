package com.gmail.molnardad.quester.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterWorldObjective")
public final class WorldObjective implements Objective {

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
	public int getTargetAmount() {
		return 1;
	}

	@Override
	public boolean isComplete(Player player, int progress) {
		return progress > 0;
	}

	@Override
	public boolean finish(Player player) {
		return true;
	}
	
	@Override
	public String progress(int progress) {
		return "Visit world '" + worldName + "'.";
	}
	
	@Override
	public String toString() {
		return TYPE+": "+worldName;
	}
	
	public boolean checkWorld(String wName) {
		return wName.equalsIgnoreCase(worldName);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("world", worldName);
		
		return map;
	}

	public static WorldObjective deserialize(Map<String, Object> map) {
		String world;
		try {
			world = (String) map.get("world");
			if(Bukkit.getWorld(world) == null)
				return null;
			
			return new WorldObjective(world);
		} catch (Exception e) {
			return null;
		}
	}
}
