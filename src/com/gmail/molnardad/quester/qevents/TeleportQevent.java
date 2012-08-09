package com.gmail.molnardad.quester.qevents;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.molnardad.quester.utils.Util;

@SerializableAs("QuesterTeleportQevent")
public final class TeleportQevent extends Qevent {

	private final String TYPE = "TELE";
	private final Location location;
	
	public TeleportQevent(int occ, int del, Location loc) {
		super(occ, del);
		this.location = loc;
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
		
		map.put("occasion", occasion);
		map.put("delay", delay);
		map.put("location", Util.serializeLocation(location));
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static TeleportQevent deserialize(Map<String, Object> map) {
		int occ, del;
		Location loc = null;
		try {
			occ = (Integer) map.get("occasion");
			del = (Integer) map.get("delay");
			
			if(map.get("location") != null)
				loc = Util.deserializeLocation((Map<String, Object>) map.get("location"));
			if(loc == null)
				return null;
		} catch (Exception e) {
			return null;
		}
		
		return new TeleportQevent(occ, del, loc);
	}

	@Override
	public void run(Player player) {
		Location loc = player.getLocation().clone();
		loc.setY(loc.getY()+1);
		player.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0);
		player.teleport(location, TeleportCause.PLUGIN);
		loc = location.clone();
		loc.setY(loc.getY()+1);
		player.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 1);
	}
}
