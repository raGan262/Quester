package com.gmail.molnardad.quester.rewards;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public final class TeleportReward implements Reward {

	private static final long serialVersionUID = 13604L;
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

}
