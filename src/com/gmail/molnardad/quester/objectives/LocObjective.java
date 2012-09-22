package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.utils.Util;

public final class LocObjective extends Objective {

	public static final String TYPE = "LOCATION";
	private final Location location;
	private final int range;
	
	public LocObjective(Location loc, int rng) {
		location = loc;
		range = rng;
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
		String locStr = String.format("%d blocks close to %.1f %.1f %.1f("+location.getWorld().getName()+")", range, location.getX(), location.getY(), location.getZ());
		return "Come at least " + locStr + ".";
	}
	
	@Override
	public String toString() {
		return TYPE+": "+Util.serializeLocString(location)+"; RNG: "+ range + coloredDesc() + stringQevents();
	}

	public boolean checkLocation(Location loc) {
		if(loc.getWorld().getName().equalsIgnoreCase(location.getWorld().getName())) {
			return loc.distance(location) < range;
		} else {
			return false;
		}
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		
		section.set("location", Util.serializeLocString(location));
		if(range != 5)
			section.set("range", range);
	}
	
	public static Objective deser(ConfigurationSection section) {
		Location loc = null;
		int rng = 3;
		loc = Util.deserializeLocString(section.getString("location", ""));
		if(loc == null)
			return null;
		if(section.isInt("range"))
			rng = section.getInt("range");
		if(rng < 1)
			rng = 3;
		return new LocObjective(loc, rng);
	}
}
