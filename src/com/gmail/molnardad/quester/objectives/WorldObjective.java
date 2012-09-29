package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public final class WorldObjective extends Objective {

	public static final String TYPE = "WORLD";
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
		return TYPE+": "+worldName + coloredDesc();
	}
	
	public boolean checkWorld(String wName) {
		return wName.equalsIgnoreCase(worldName);
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		
		section.set("world", worldName);
	}
	
	public static Objective deser(ConfigurationSection section) {
		String world = null;
		if(section.isString("world"))
			world = section.getString("world");
		if(world == null)
			return null;
		return new WorldObjective(world);
	}
}
