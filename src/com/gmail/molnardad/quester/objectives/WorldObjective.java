package com.gmail.molnardad.quester.objectives;

import org.bukkit.entity.Player;

public final class WorldObjective implements Objective {

	private static final long serialVersionUID = 13505L;
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

}
