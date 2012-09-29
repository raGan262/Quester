package com.gmail.molnardad.quester.qevents;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.Util;

public final class ExplosionQevent extends Qevent {

	public static final String TYPE = "EXPLOSION";
	private final Location location;
	private final boolean damage;
	private final int range;
	
	public ExplosionQevent(int occ, int del, Location loc, int rng, boolean dmg) {
		super(occ, del);
		this.location = loc;
		this.damage = dmg;
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
		return TYPE + ": " + locStr + "; RNG: " + range + "; DMG: " + damage;
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
	
	public static ExplosionQevent deser(int occ, int del, ConfigurationSection section) {
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
		
		return new ExplosionQevent(occ, del, loc, rng, dmg);
	}

	@Override
	public void run(Player player) {
		Location loc;
		if(location == null)
			loc = Util.move(player.getLocation(), range);
		else
			loc = Util.move(location, range);
		
		if(damage)
			loc.getWorld().createExplosion(loc, 4F);
		else
			loc.getWorld().createExplosion(loc, 0F);
	}
}
