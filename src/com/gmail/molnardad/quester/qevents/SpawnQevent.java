package com.gmail.molnardad.quester.qevents;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.Util;

@SerializableAs("QuesterSpawnQevent")
public final class SpawnQevent extends Qevent {

	private final String TYPE = "SPAWN";
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
		return TYPE + ": ON-" + parseOccasion(occasion) + "; LOC: " + locStr + "; RNG: " + range + "; ENT: " + entity.getName() + "; AMT: " + amount;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("entity", entity.getTypeId());
		map.put("amount", amount);
		map.put("occasion", occasion);
		map.put("delay", delay);
		map.put("location", Util.serializeLocation(location));
		map.put("range", range);
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static SpawnQevent deserialize(Map<String, Object> map) {
		int occ, del, rng = 0, amt = 1;
		EntityType ent = null;
		Location loc = null;
		try {
			occ = (Integer) map.get("occasion");
			del = (Integer) map.get("delay");
			ent = EntityType.fromId((Integer) map.get("entity"));
			
			if(map.get("location") != null)
				loc = Util.deserializeLocation((Map<String, Object>) map.get("location"));
			if(map.get("range") != null)
				rng = (Integer) map.get("range");
			if(rng < 0)
				rng = 0;
			if(map.get("amount") != null)
				amt = (Integer) map.get("amount");
				
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
