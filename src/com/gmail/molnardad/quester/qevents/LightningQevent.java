package com.gmail.molnardad.quester.qevents;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.Util;

@SerializableAs("QuesterLightningQevent")
public final class LightningQevent extends Qevent {

	private final String TYPE = "LIGHTNING";
	private final Location location;
	private final boolean damage;
	
	public LightningQevent(int occ, int del, Location loc, boolean damage) {
		super(occ, del);
		this.location = loc;
		this.damage = damage;
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
		String locStr = String.format("%.1f:%.1f:%.1f("+location.getWorld().getName()+")", location.getX(), location.getY(), location.getZ());
		return TYPE + ": ON-" + parseOccasion(occasion) + "; LOC: " + locStr + "; DMG: " + damage;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("damage", damage);
		map.put("occasion", occasion);
		map.put("delay", delay);
		map.put("location", Util.serializeLocation(location));
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static LightningQevent deserialize(Map<String, Object> map) {
		int occ, del;
		boolean damage;
		Location loc = null;
		try {
			occ = (Integer) map.get("occasion");
			damage = (Boolean) map.get("damage");
			del = (Integer) map.get("delay");
			
			if(map.get("location") != null)
				loc = Util.deserializeLocation((Map<String, Object>) map.get("location"));
			if(loc == null)
				return null;
		} catch (Exception e) {
			return null;
		}
		
		return new LightningQevent(occ, del, loc, damage);
	}

	@Override
	public void run(Player player) {
		if(damage)
			location.getWorld().strikeLightning(location);
		else
			location.getWorld().strikeLightningEffect(location);
	}
}
