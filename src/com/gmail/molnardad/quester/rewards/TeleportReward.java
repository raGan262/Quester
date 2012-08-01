package com.gmail.molnardad.quester.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@SerializableAs("QuesterTeleportReward")
public final class TeleportReward implements Reward {

	private final String TYPE = "TELEPORT";
	private final double x;
	private final double y;
	private final double z;
	private final String worldName;

	public TeleportReward(Location loc) {
		x = loc.getX();
		y = loc.getY();
		z = loc.getZ();
		worldName = loc.getWorld().getName();
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean checkReward(Player player) {
		return true;
	}

	@Override
	public boolean giveReward(Player player) {
		World world = Bukkit.getWorld(worldName) == null ? player.getWorld() : Bukkit.getWorld(worldName);
		return player.teleport(new Location(world, x, y, z), TeleportCause.PLUGIN);
	}

	@Override
	public String checkErrorMessage() {
		return "TeleportReward checkErrorMessage()";
	}

	@Override
	public String giveErrorMessage() {
		return "For some reason, teleport failed.";
	}
	
	@Override
	public String toString() {
		String locStr = String.format("X:%.1f Y:%.1f Z:%.1f", x, y, z);
		return TYPE+": World: '"+worldName+"' "+locStr;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("x", x);
		map.put("y", y);
		map.put("z", z);
		map.put("world", worldName);
		
		return map;
	}

	public static TeleportReward deserialize(Map<String, Object> map) {
		double x, y, z;
		String world;
		
		try {
			x = (Double) map.get("x");
			y = (Double) map.get("y");
			z = (Double) map.get("z");
			world = (String) map.get("world");	
			if(Bukkit.getWorld(world) == null)
				return null;
			return new TeleportReward(new Location(Bukkit.getWorld(world), x, y, z));
		} catch (Exception e) {
			return null;
		}
	}

}
