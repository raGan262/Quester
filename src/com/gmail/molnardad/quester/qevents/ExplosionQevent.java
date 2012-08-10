package com.gmail.molnardad.quester.qevents;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.Util;

@SerializableAs("QuesterExplosionQevent")
public final class ExplosionQevent extends Qevent {

	private final String TYPE = "EXPLOSION";
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
		return TYPE + ": ON-" + parseOccasion(occasion) + "; LOC: " + locStr + "; RNG: " + range + "; DMG: " + damage;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("damage", damage);
		map.put("occasion", occasion);
		map.put("delay", delay);
		map.put("location", Util.serializeLocation(location));
		map.put("range", range);
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static ExplosionQevent deserialize(Map<String, Object> map) {
		int occ, del, rng = 0;
		boolean dmg;
		Location loc = null;
		try {
			occ = (Integer) map.get("occasion");
			dmg = (Boolean) map.get("damage");
			del = (Integer) map.get("delay");
			if(map.get("range") != null)
				rng = (Integer) map.get("range");
			
			if(map.get("location") != null)
				loc = Util.deserializeLocation((Map<String, Object>) map.get("location"));
		} catch (Exception e) {
			return null;
		}
		
		return new ExplosionQevent(occ, del, loc, rng, dmg);
	}

	@Override
	public void run(Player player) {
		Location loc;;
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
