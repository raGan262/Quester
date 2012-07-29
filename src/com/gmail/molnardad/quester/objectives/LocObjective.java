package com.gmail.molnardad.quester.objectives;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class LocObjective implements Objective {

	private static final long serialVersionUID = 13504L;
	private final String TYPE = "LOCATION";
	private final double x;
	private final double y;
	private final double z;
	private final String worldName;
	private final int range;
	
	public LocObjective(Location loc, int rng) {
		x = loc.getX();
		y = loc.getY();
		z = loc.getZ();
		worldName = loc.getWorld().getName();
		range = rng;
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
		String locStr = String.format("%d blocks close to %.1f %.1f %.1f("+worldName+")", range, x, y, z);
		return "Come at least " + locStr + ".";
	}
	
	@Override
	public String toString() {
		String locStr = String.format("%.1f|%.1f|%.1f("+worldName+")", x, y, z);
		return TYPE+": "+locStr+"; RNG: "+String.valueOf(range);
	}

	public boolean checkLocation(Location loc) {
		if(loc.getWorld().getName().equalsIgnoreCase(worldName)) {
			return loc.distance(new Location(loc.getWorld(), x, y, z)) < range;
		} else {
			return false;
		}
	}
}
