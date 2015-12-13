package me.ragan262.quester.utils;

import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.storage.StorageKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public abstract class Region {
	
	public static final Region ANYWHERE = new Anywhere();
	static final String SEPARATOR = "|";
	
	public abstract String getType();
	
	public abstract boolean isWithin(Location location);
	
	@Override
	public abstract String toString();
	
	public abstract String serializeToString();
	
	public static Region fromString(final CommandSender sender, final String string) {
		try {
			final String[] strs = string.split(Pattern.quote(SEPARATOR));
			if(strs[0].equalsIgnoreCase("SPHERE")) {
				return new Sphere(SerUtils.getLoc(sender, strs[1]), Double.valueOf(strs[2]));
			}
			else if(strs[0].equalsIgnoreCase("CUBOID")) {
				return new Cuboid(SerUtils.getLoc(sender, strs[1]), SerUtils.getLoc(sender, strs[2]));
			}
			else if(strs[0].equalsIgnoreCase("WORLD")) {
				if(sender instanceof Player
						&& strs[1].equalsIgnoreCase(QConfiguration.worldLabelThis)) {
					strs[1] = ((Player)sender).getWorld().getName();
				}
				return new World(strs[1]);
			}
			else if(strs[0].equalsIgnoreCase("ANYWHERE")) {
				return Region.ANYWHERE;
			}
		}
		catch(final Exception ignore) {}
		return null;
	}
	
	public void serialize(final StorageKey key) {
		key.setString("", getType() + SEPARATOR + serializeToString());
	}
	
	public static Region deserialize(final StorageKey key) {
		return fromString(null, key.getString("", null));
	}
	
	private static class Anywhere extends Region {
		
		@Override
		public String getType() {
			return "ANYWHERE";
		}
		
		@Override
		public boolean isWithin(final Location location) {
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
		
		private final QLocation center;
		private final double range;
		private final double powRange;
		
		public Sphere(final QLocation center, final double range) {
			if(center == null) {
				throw new IllegalArgumentException("Location cannot be null.");
			}
			this.center = center;
			this.range = range;
			powRange = range * range;
		}
		
		@Override
		public String getType() {
			return "SPHERE";
		}
		
		@Override
		public boolean isWithin(final Location location) {
			if(location == null) {
				return false;
			}
			Location loc = center.getLocation();
			if(location.getWorld().getUID() != loc.getWorld().getUID()) {
				return false;
			}
			return loc.distanceSquared(location) <= powRange;
		}
		
		@Override
		public String toString() {
			return getType() + " " + SerUtils.displayLocation(center) + " R=" + range;
		}
		
		@Override
		public String serializeToString() {
			return SerUtils.serializeLocString(center) + SEPARATOR + range;
		}
	}
	
	public static class Cuboid extends Region {
		
		private final QLocation min;
		private final QLocation max;
		
		public Cuboid(final QLocation loc1, final QLocation loc2) {
			if(loc1 == null || loc2 == null) {
				throw new IllegalArgumentException("Locations cannot be null.");
			}
			if(!loc1.getWorldName().equalsIgnoreCase(loc2.getWorldName())) {
				throw new IllegalArgumentException("Locations must be within the same world.");
			}
			min = new QLocation(loc1.getWorldName(), Math.min(loc1.getX(), loc2.getX()), Math.min(loc1.getY(), loc2.getY()), Math.min(loc1.getZ(), loc2.getZ()), 0, 0);
			max = new QLocation(loc1.getWorldName(), Math.max(loc1.getX(), loc2.getX()), Math.max(loc1.getY(), loc2.getY()), Math.max(loc1.getZ(), loc2.getZ()), 0, 0);
		}
		
		@Override
		public String getType() {
			return "CUBOID";
		}
		
		@Override
		public boolean isWithin(final Location location) {
			if(location == null || !min.getWorldName().equalsIgnoreCase(location.getWorld().getName())) {
				return false;
			}
			
			return min.getX() <= location.getX() && min.getY() <= location.getY()
					&& min.getZ() <= location.getZ() && max.getX() >= location.getX()
					&& max.getY() >= location.getY() && max.getZ() >= location.getZ();
		}
		
		@Override
		public String toString() {
			return getType() + " " + SerUtils.displayLocation(min) + " "
					+ SerUtils.displayLocation(max);
		}
		
		@Override
		public String serializeToString() {
			return SerUtils.serializeLocString(min) + SEPARATOR + SerUtils.serializeLocString(max);
		}
	}
	
	public static class World extends Region {
		
		final String world;
		
		public World(final String worldName) {
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
		public boolean isWithin(final Location location) {
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
