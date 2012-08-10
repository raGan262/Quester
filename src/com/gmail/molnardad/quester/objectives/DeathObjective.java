package com.gmail.molnardad.quester.objectives;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterDeathObjective")
public final class DeathObjective extends Objective {

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
	public String progress(int progress) {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%r", String.valueOf(amount - progress)).replaceAll("%t", String.valueOf(amount));
		}
		String locStr = y < 0 ? "anywhere " : String.format("max %d blocks from %.1f %.1f %.1f("+worldName+") ", range, x, y, z);
		return "Die " + locStr + String.valueOf(amount - progress)+"x.";
	}
	
	@Override
	public String toString() {
		String locStr = y < 0 ? "ANY" : String.format("%.1f %.1f %.1f("+worldName+")", x, y, z);
		return TYPE + ": LOC: " + locStr + "; AMT: "+ amount +"; RNG: "+ range + stringQevents();
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

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		
		map.put("x", x);
		map.put("y", y);
		map.put("z", z);
		map.put("world", worldName);
		map.put("amount", amount);
		map.put("range", range);
		
		return map;
	}

	public static DeathObjective deserialize(Map<String, Object> map) {
		double x, y, z;
		String world;
		Location loc = null;
		int amt, rng;
		
		try {
			y = (Double) map.get("y");
			if(y >= 0) {
				x = (Double) map.get("x");
				z = (Double) map.get("z");
				world = (String) map.get("world");	
				if(Bukkit.getWorld(world) != null)
					loc = new Location(Bukkit.getWorld(world), x, y, z);
				else
					return null;
			}
			amt = (Integer) map.get("amount");
			if(amt < 1)
				return null;
			rng = (Integer) map.get("range");
			if(rng < 1)
				return null;
			DeathObjective obj = new DeathObjective(amt, loc, rng);
			obj.loadSuper(map);
			return obj;
		} catch (Exception e) {
			return null;
		}
	}

}
