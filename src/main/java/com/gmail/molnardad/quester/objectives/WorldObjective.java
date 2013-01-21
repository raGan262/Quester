package com.gmail.molnardad.quester.objectives;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;

@QElement("WORLD")
public final class WorldObjective extends Objective {

	private final String worldName;
	
	public WorldObjective(String worldName) {
		this.worldName = worldName;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(int progress) {
		return "Visit world '" + worldName + "'.";
	}
	
	@Override
	protected String info() {
		return worldName;
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
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
	
	//Custom methods
	
	public boolean checkWorld(String wName) {
		return wName.equalsIgnoreCase(worldName);
	}
}
