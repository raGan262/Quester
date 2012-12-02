package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.exceptions.QuesterException;
public final class NpcObjective extends Objective {

	public static final String TYPE = "NPC";
	private final int index;
	private final boolean cancel;
	
	public NpcObjective(int id, boolean ccl) {
		index = id;
		cancel = ccl;
	}
	
	public boolean getCancel() {
		return cancel;
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
		return TYPE + ": " + index + "; CANCEL: " + cancel + coloredDesc();
	}
	
	public boolean checkNpc(int npc) {
		return npc == index;
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		
		section.set("index", index);
		if(cancel) {
			section.set("cancel", cancel);
		}
	}
	
	public static Objective deser(ConfigurationSection section) throws QuesterException {
		int id = -1;
		boolean ccl = false;
		id = section.getInt("index", -1);
		if(id < 0)
			return null;
		ccl = section.getBoolean("cancel", false);
		return new NpcObjective(id, ccl);
	}
}
