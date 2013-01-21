package com.gmail.molnardad.quester.objectives;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.utils.Util;

@QElement("LOCATION")
public final class LocObjective extends Objective {

	private final Location location;
	private final int range;
	
	public LocObjective(Location loc, int rng) {
		location = loc;
		range = rng;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(int progress) {
		String locStr = String.format("%d blocks close to %.1f %.1f %.1f("+location.getWorld().getName()+")", range, location.getX(), location.getY(), location.getZ());
		return "Come at least " + locStr + ".";
	}
	
	@Override
	protected String info() {
		return Util.serializeLocString(location) + "; RNG: " + range;
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
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
	
	//Custom methods

	public boolean checkLocation(Location loc) {
		if(loc.getWorld().getName().equalsIgnoreCase(location.getWorld().getName())) {
			return loc.distanceSquared(location) < range*range;
		} else {
			return false;
		}
	}
}
