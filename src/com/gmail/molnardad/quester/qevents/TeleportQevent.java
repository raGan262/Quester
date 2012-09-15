package com.gmail.molnardad.quester.qevents;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.molnardad.quester.utils.Util;

public final class TeleportQevent extends Qevent {

	public static final String TYPE = "TELEPORT";
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
		return TYPE + ": LOC: " + locStr;
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section);
		section.set("location", Util.serializeLocString(location));
		
	}
	
	public static TeleportQevent deser(int occ, int del, ConfigurationSection section) {
		Location loc = null;
		try {
			if(section.isString("location"))
				loc = Util.deserializeLocString(section.getString("location"));
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
