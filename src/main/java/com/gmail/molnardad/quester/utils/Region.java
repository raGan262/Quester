package com.gmail.molnardad.quester.utils;

import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.storage.StorageKey;

public abstract class Region {
	
	public static Region ANYWHERE = new Anywhere();
	static final String SEPARATOR = "|";
	
	public abstract String getType();
	
	public abstract boolean isWithin(Location location);
	
	public abstract String toString();
	
	public abstract String serializeToString();
	
	public static Region fromString(CommandSender sender, String string) {
		try {
			String[] strs = string.split(Pattern.quote(SEPARATOR));
			if(strs[0].equalsIgnoreCase("SPHERE")) {
				return new Sphere(Util.getLoc(sender, strs[1]), Double.valueOf(strs[2]));
			}
			else if(strs[0].equalsIgnoreCase("CUBOID")) {
				return new Cuboid(Util.getLoc(sender, strs[1]), Util.getLoc(sender, strs[2]));
			}
			else if(strs[0].equalsIgnoreCase("WORLD")) {
				if(sender instanceof Player && strs[1].equalsIgnoreCase(QConfiguration.worldLabelThis)) {
					strs[1] = ((Player) sender).getWorld().getName();
				}
				return new World(strs[1]);
			}
			else if(strs[0].equalsIgnoreCase("ANYWHERE")) {
				return Region.ANYWHERE;
			}
		}
		catch (Exception ignore) {}
		return null;
	}
	
	public void serialize(StorageKey key) {
		key.setString("", getType() + SEPARATOR + serializeToString());
	}
	
	public static Region deserialize(StorageKey key) {
		return fromString(null, key.getString("", null));
	}
	
	private static class Anywhere extends Region {

		@Override
		public String getType() {
			return "ANYWHERE";
		}

		@Override
		public boolean isWithin(Location location) {
			return true;
		}

		@Override
		public String toString() {
			return getType();
		}

		@Override
		public String serializeToString() {
			return "";
		}
		
	}
	
	public static class Sphere extends Region {

		private final Location center;
		private final double range;
		private final double powRange;
		
		public Sphere(Location center, double range) {
			if(center == null) {
				throw new IllegalArgumentException("Location cannot be null.");
			}
			this.center = center;
			this.range = range;
			this.powRange = range * range;
		}
		
		@Override
		public String getType() {
			return "SPHERE";
		}
		
		@Override
		public boolean isWithin(Location location) {
			if(location == null) {
				return false;
			}
			return center.distanceSquared(location) <= powRange;
		}

		@Override
		public String toString() {
			return getType() + " " + Util.displayLocation(center) + " R=" + range;
		}

		@Override
		public String serializeToString() {
			return Util.serializeLocString(center) + SEPARATOR + range;
		}
	}
	
	public static class Cuboid extends Region {

		private final Location min;
		private final Location max;
		
		public Cuboid(Location loc1, Location loc2) {
			if(loc1 == null || loc2 == null) {
				throw new IllegalArgumentException("Locations cannot be null.");
			}
			if(!loc1.getWorld().equals(loc2.getWorld())) {
				throw new IllegalArgumentException("Locations must be within the same world.");
			}
			this.min = new Location(loc1.getWorld(), Math.min(loc1.getX(), loc2.getX()), Math.min(loc1.getY(), loc2.getY()), Math.min(loc1.getZ(), loc2.getZ()));
			this.max = new Location(loc1.getWorld(), Math.max(loc1.getX(), loc2.getX()), Math.max(loc1.getY(), loc2.getY()), Math.max(loc1.getZ(), loc2.getZ()));
		}
		
		@Override
		public String getType() {
			return "CUBOID";
		}
		
		@Override
		public boolean isWithin(Location location) {
			if(location == null || !min.getWorld().equals(location.getWorld())) {
				return false;
			}
			
			return min.getX() <= location.getX()
					&& min.getY() <= location.getY()
					&& min.getZ() <= location.getZ()
					&& max.getX() >= location.getX()
					&& max.getY() >= location.getY()
					&& max.getZ() >= location.getZ();
		}

		@Override
		public String toString() {
			return getType() + " " + Util.displayLocation(min) + " " + Util.displayLocation(max);
		}

		@Override
		public String serializeToString() {
			return Util.serializeLocString(min) + SEPARATOR + Util.serializeLocString(max);
		}
	}
	
	public static class World extends Region {

		final String world;
		
		public World(String worldName) {
			if(Bukkit.getWorld(worldName) == null) {
				throw new IllegalArgumentException("Invalid world.");
			}
			world = worldName;
		}
		
		@Override
		public String getType() {
			return "WORLD";
		}

		@Override
		public boolean isWithin(Location location) {
			if(location == null) {
				return false;
			}
			return location.getWorld().getName().equalsIgnoreCase(world);
		}

		@Override
		public String toString() {
			return getType() + " " + world;
		}

		@Override
		public String serializeToString() {
			return world;
		}
		
	}
}
