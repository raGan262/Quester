package com.gmail.molnardad.quester.objectives;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
public final class NpcObjective extends Objective {

	public static final String TYPE = "NPC";
	private final int index;
	
	public NpcObjective(int id) {
		index = id;
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
		return "Interact with NPC ID " + index + ".";
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + index + coloredDesc();
	}
	
	public boolean checkNpc(NPC npc) {
		return npc.getId() == index;
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		
		section.set("index", index);
	}
	
	public static Objective deser(ConfigurationSection section) {
		int id = 0;
		if(section.isInt("index"))
			id = section.getInt("index");
		if(id < 1)
			return null;
		return new NpcObjective(id);
	}
}
