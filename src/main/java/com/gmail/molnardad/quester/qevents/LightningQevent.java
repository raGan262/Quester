package com.gmail.molnardad.quester.qevents;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.Util;

public final class LightningQevent extends Qevent {

	public static final String TYPE = "LIGHTNING";
	private final Location location;
	private final boolean damage;
	private final int range;
	
	public LightningQevent(int occ, int del, Location loc, int rng, boolean damage) {
		super(occ, del);
		this.location = loc;
		this.damage = damage;
		this.range = rng;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public int getOccasion() {
		return occasion;
	}
	
	@Override
	public String toString() {
		String locStr;
		if(location == null)
			locStr = "PLAYER";
		else
			locStr = String.format("%.1f:%.1f:%.1f("+location.getWorld().getName()+")", location.getX(), location.getY(), location.getZ());
		return TYPE + ": " + locStr + "; RNG: " + range + "; DMG: " + damage + appendSuper();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		if(damage)
			section.set("damage", damage);
		section.set("location", Util.serializeLocString(location));
		if(range != 0)
			section.set("range", range);
	}
	
	public static LightningQevent deser(int occ, int del, ConfigurationSection section) {
		int rng = 0;
		boolean dmg = false;
		Location loc = null;
		try {
			if(section.isString("location")) {
				loc = Util.deserializeLocString(section.getString("location"));
			}
			if(section.isInt("range")) {
				rng = section.getInt("range");
				if(rng < 0)
					rng = 0;
			}
			if(section.isBoolean("damage"))
				dmg = section.getBoolean("damage");
			
		} catch (Exception e) {
			return null;
		}
		
		return new LightningQevent(occ, del, loc, rng, dmg);
	}

	@Override
	void run(Player player) {
		Location loc;
		if(location == null)
			loc = Util.move(player.getLocation(), range);
		else
			loc = Util.move(location, range);
		
		if(damage)
			loc.getWorld().strikeLightning(loc);
		else
			loc.getWorld().strikeLightningEffect(loc);
	}
}
