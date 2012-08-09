package com.gmail.molnardad.quester.qevents;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.Util;

@SerializableAs("QuesterSetBlockQevent")
public final class SetBlockQevent extends Qevent {

	private final String TYPE = "BLOCK";
	private final Location location;
	public final int material;
	public final byte data;
	
	public SetBlockQevent(int occ, int del, int mat, int dat, Location loc) {
		super(occ, del);
		this.location = loc;
		this.material = mat;
		this.data = (byte) dat;
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
		return TYPE + ": ON-" + parseOccasion(occasion) + "; LOC: " + locStr;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("delay", delay);
		map.put("occasion", occasion);
		map.put("material", material);
		map.put("data", data);
		map.put("location", Util.serializeLocation(location));
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static SetBlockQevent deserialize(Map<String, Object> map) {
		int occ, del, mat, dat;
		Location loc = null;
		try {
			occ = (Integer) map.get("occasion");
			del = (Integer) map.get("delay");
			mat = (Integer) map.get("material");
			dat = (Integer) map.get("data");
			
			if(map.get("location") != null)
				loc = Util.deserializeLocation((Map<String, Object>) map.get("location"));
			if(loc == null)
				return null;
		} catch (Exception e) {
			return null;
		}
		
		return new SetBlockQevent(occ, del, mat, dat, loc);
	}

	@Override
	public void run(Player player) {
		location.getBlock().setTypeIdAndData(material, data, true);
	}
}
