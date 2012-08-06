package com.gmail.molnardad.quester.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterLocObjective")
public final class LocObjective implements Objective {

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

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("x", x);
		map.put("y", y);
		map.put("z", z);
		map.put("world", worldName);
		map.put("range", range);
		
		return map;
	}

	public static LocObjective deserialize(Map<String, Object> map) {
		double x, y, z;
		String world;
		Location loc = null;
		int rng;
		
		try {
			y = (Double) map.get("y");
			if(y < 0)
				return null;
			x = (Double) map.get("x");
			z = (Double) map.get("z");
			world = (String) map.get("world");	
			if(Bukkit.getWorld(world) != null)
				loc = new Location(Bukkit.getWorld(world), x, y, z);
			else
				return null;
			rng = (Integer) map.get("range");
			if(rng < 1)
				return null;
			
			return new LocObjective(loc, rng);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean tryToComplete(Player player) {
		return false;
	}
}
