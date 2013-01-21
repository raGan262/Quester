package com.gmail.molnardad.quester.objectives;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.utils.Util;

@QElement("DEATH")
public final class DeathObjective extends Objective {

	private final Location location;
	private final int amount;
	private final int range;
	
	public DeathObjective(int amt, Location loc, int rng) {
		amount = amt;
		range = rng;
		location = loc;
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(int progress) {
		String locStr = location == null ? "anywhere " : String.format("max %d blocks from %.1f %.1f %.1f("+location.getWorld().getName()+") ", range, location.getX(), location.getY(), location.getZ());
		return "Die " + locStr + String.valueOf(amount - progress)+"x.";
	}
	
	@Override
	protected String info() {
		String locStr = location == null ? "ANY" : String.format("%.1f %.1f %.1f("+location.getWorld().getName()+")", location.getX(), location.getY(), location.getZ());
		return "LOC: " + locStr + "; AMT: "+ amount +"; RNG: "+ range;
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		if(location != null)
			section.set("location", Util.serializeLocString(location));
		if(amount != 1)
			section.set("amount", amount);
		if(range != 5)
			section.set("range", range);
	}
	
	public static Objective deser(ConfigurationSection section) {
		Location loc = null;
		int amt = 1, rng = 5;
		if(section.isString("location"))
			loc = Util.deserializeLocString(section.getString("location"));
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		if(amt < 1)
			amt = 1;
		if(section.isInt("range"))
			rng = section.getInt("range");
		if(rng < 1)
			rng = 5;
		return new DeathObjective(amt, loc, rng);
	}
	
	// Custom methods
	
	public boolean checkDeath(Location loc) {
		if(location == null) {
			return true;
		}
		if(loc.getWorld().getName().equals(location.getWorld().getName())) {
			return loc.distance(location) < range;
		} 
		return false;
	}
}
