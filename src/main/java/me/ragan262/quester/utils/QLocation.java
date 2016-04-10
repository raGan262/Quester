package me.ragan262.quester.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class QLocation {

	private Location location = null;
	private final String world;
	private final double x, y, z;
	private final float yaw, pitch;

	public QLocation(String world, double x, double y, double z, float yaw, float pitch) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public QLocation(final Location location) {
		this.location = location;
		world = location.getWorld().getName();
		x = location.getX();
		y = location.getY();
		z = location.getZ();
		yaw = location.getYaw();
		pitch = location.getPitch();
	}

	public Location getLocation() {
		if(location != null) {
			return location;
		}
		World worldObject = Bukkit.getWorld(world);
		if(worldObject == null) {
			throw new RuntimeException("Invalid world.");
		}
		return new Location(worldObject, x, y, z, yaw, pitch);
	}

	public String getWorldName() {
		return world;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}
}
