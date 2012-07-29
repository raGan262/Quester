package com.gmail.molnardad.quester.objectives;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class DeathObjective implements Objective {

	private static final long serialVersionUID = 13501L;
	private final String TYPE = "DEATH";
	private final double x;
	private final double y;
	private final double z;
	private final String worldName;
	private final int amount;
	private final int range;
	
	public DeathObjective(int amt, Location loc, int rng) {
		amount = amt;
		range = rng;
		if(loc != null) {
			x = loc.getX();
			y = loc.getY();
			z = loc.getZ();
			worldName = loc.getWorld().getName();
		} else {
			x = 0;
			y = -1;
			z = 0;
			worldName = "";
		}
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}

	@Override
	public boolean isComplete(Player player, int progress) {
		return progress >= amount;
	}

	@Override
	public boolean finish(Player player) {
		return true;
	}
	
	@Override
	public String progress(int progress) {
		String locStr = y < 0 ? "anywhere " : String.format("max %d blocks from %.1f %.1f %.1f("+worldName+") ", range, x, y, z);
		return "Die " + locStr + String.valueOf(amount - progress)+"x.";
	}
	
	@Override
	public String toString() {
		String locStr = y < 0 ? "ANY" : String.format("%.1f %.1f %.1f("+worldName+")", x, y, z);
		return TYPE + ": LOC: "+locStr+"; AMT: "+String.valueOf(amount)+"; RNG: "+String.valueOf(range);
	}
	
	public boolean checkDeath(Location loc) {
		if(y < 0) {
			return true;
		}
		if(loc.getWorld().getName().equalsIgnoreCase(worldName)) {
			return loc.distance(new Location(loc.getWorld(), x, y, z)) < range;
		} 
		return false;
	}

}
