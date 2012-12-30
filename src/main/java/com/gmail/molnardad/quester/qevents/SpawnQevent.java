package com.gmail.molnardad.quester.qevents;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.Util;

public final class SpawnQevent extends Qevent {

	public static final String TYPE = "SPAWN";
	private final Location location;
	private final EntityType entity;
	private final int range;
	private final int amount;
	
	public SpawnQevent(int occ, int del, Location loc, int rng, EntityType ent, int amt) {
		super(occ, del);
		this.location = loc;
		this.range = rng;
		this.entity = ent;
		this.amount = amt;
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
		return TYPE + ": " + entity.getName() + "; AMT: " + amount + "; LOC: " + locStr + "; RNG: " + range + appendSuper();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		if(amount != 1)
			section.set("amount", amount);
		section.set("entity", entity.getTypeId());
		section.set("location", Util.serializeLocString(location));
		if(range != 0)
			section.set("range", range);
	}
	
	public static SpawnQevent deser(int occ, int del, ConfigurationSection section) {
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
		
		return new SpawnQevent(occ, del, loc, rng, ent, amt);
	}

	@Override
	public void run(Player player) {
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
