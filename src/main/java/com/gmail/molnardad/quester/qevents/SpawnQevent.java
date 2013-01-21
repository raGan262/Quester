package com.gmail.molnardad.quester.qevents;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.utils.Util;

@QElement("SPAWN")
public final class SpawnQevent extends Qevent {

	private final Location location;
	private final EntityType entity;
	private final int range;
	private final int amount;
	
	public SpawnQevent(Location loc, int rng, EntityType ent, int amt) {
		this.location = loc;
		this.range = rng;
		this.entity = ent;
		this.amount = amt;
	}
	
	@Override
	public String info() {
		String locStr = "PLAYER";
		if(location != null) {
			locStr = Util.displayLocation(location);
		}
		return entity.getName() + "; AMT: " + amount + "; LOC: " + locStr + "; RNG: " + range;
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		if(amount != 1)
			section.set("amount", amount);
		section.set("entity", entity.getTypeId());
		section.set("location", Util.serializeLocString(location));
		if(range != 0)
			section.set("range", range);
	}
	
	public static SpawnQevent deser(ConfigurationSection section) {
		int rng = 0, amt = 1;
		EntityType ent = null;
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
			amt = section.getInt("amount", 1);
			try {
				ent = Util.parseEntity(section.getString("entity"));
			} catch (Exception ignore) {}
			if(ent == null)
				return null;
		} catch (Exception e) {
			return null;
		}
		
		return new SpawnQevent(loc, rng, ent, amt);
	}

	@Override
	protected void run(Player player) {
		Location temp;
		if(location == null)
			temp = player.getLocation();
		else
			temp = location;
		for(int i = 0; i < amount; i++) {
			temp.getWorld().spawnEntity(Util.move(temp, range), entity);
		}
	}
}
