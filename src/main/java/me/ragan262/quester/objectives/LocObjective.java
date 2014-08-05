package me.ragan262.quester.objectives;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Region;
import me.ragan262.quester.utils.SerUtils;
import org.bukkit.Location;

/* DEPRECATED - use REGION objective instead */

@QElement("LOCATION")
public final class LocObjective extends Objective {
	
	private final Location location;
	private final int range;
	
	public LocObjective(final Location loc, final int rng) {
		location = loc;
		range = rng;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(final int progress) {
		final String locStr = String.format("%d blocks close to %.1f %.1f %.1f("
				+ location.getWorld().getName() + ")", range, location.getX(), location.getY(), location.getZ());
		return "Come at least " + locStr + ".";
	}
	
	@Override
	protected String info() {
		return SerUtils.serializeLocString(location) + "; RNG: " + range;
	}
	
	@Command(min = 1, max = 2, usage = "{<location>} [range]")
	public static Objective fromCommand(final QuesterCommandContext context) throws CommandException {
		int rng = 3;
		final Location loc = SerUtils.getLoc(context.getPlayer(), context.getString(0));
		if(loc == null) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_LOC_INVALID"));
		}
		if(context.length() > 1) {
			rng = context.getInt(1);
			if(rng < 1) {
				throw new CommandException(context.getSenderLang().get("ERROR_CMD_RANGE_INVALID"));
			}
		}
		return new RegionObjective(new Region.Sphere(loc, rng), context.hasFlag('i'));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("location", SerUtils.serializeLocString(location));
		if(range != 3) {
			key.setInt("range", range);
		}
	}
	
	protected static Objective load(final StorageKey key) {
		final Location location = SerUtils.deserializeLocString(key.getString("location", ""));
		int range = 3;
		if(location == null) {
			return null;
		}
		range = key.getInt("range", 3);
		if(range < 1) {
			range = 3;
		}
		final Region region = new Region.Sphere(location, range);
		
		return new RegionObjective(region, key.getBoolean("inverted", false));
	}
	
	// Custom methods
	
	public boolean checkLocation(final Location loc) {
		if(loc.getWorld().getName().equalsIgnoreCase(location.getWorld().getName())) {
			return loc.distanceSquared(location) < range * range;
		}
		else {
			return false;
		}
	}
}