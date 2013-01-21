package com.gmail.molnardad.quester.qevents;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.utils.Util;

@QElement("LIGHTNING")
public final class LightningQevent extends Qevent {

	private final Location location;
	private final boolean damage;
	private final int range;
	
	public LightningQevent(Location loc, int rng, boolean damage) {
		this.location = loc;
		this.damage = damage;
		this.range = rng;
	}
	
	@Override
	public String info() {
		String locStr = "PLAYER";
		if(location != null) {
			locStr = Util.displayLocation(location);
		}
		return locStr + "; RNG: " + range + "; DMG: " + damage;
	}

	@Override
	protected void run(Player player) {
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

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		if(damage)
			section.set("damage", damage);
		section.set("location", Util.serializeLocString(location));
		if(range != 0)
			section.set("range", range);
	}
	
	public static LightningQevent deser(ConfigurationSection section) {
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
		
		return new LightningQevent(loc, rng, dmg);
	}
}
