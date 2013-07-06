package com.gmail.molnardad.quester.objectives;

import static com.gmail.molnardad.quester.utils.Util.getLoc;

import org.bukkit.Location;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Region;
import com.gmail.molnardad.quester.utils.Util;

/* DEPRECATED - use REGION objective instead */

@QElement("LOCATION")
public final class LocObjective extends Objective {

	private final Location location;
	private final int range;

	public LocObjective(Location loc, int rng) {
		location = loc;
		range = rng;
	}

	@Override
	public int getTargetAmount() {
		return 1;
	}

	@Override
	protected String show(int progress) {
		String locStr = String.format("%d blocks close to %.1f %.1f %.1f("+location.getWorld().getName()+")", range, location.getX(), location.getY(), location.getZ());
		return "Come at least " + locStr + ".";
	}

	@Override
	protected String info() {
		return Util.serializeLocString(location) + "; RNG: " + range;
	}

	@QCommand(
			min = 1,
			max = 2,
			usage = "{<location>} [range]")
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
		int rng = 3;			
		Location loc = getLoc(context.getPlayer(), context.getString(0));
		if(loc == null) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_LOC_INVALID);
		}
		if(context.length() > 1){
			rng = context.getInt(1);
			if(rng < 1) {
				throw new QCommandException(context.getSenderLang().ERROR_CMD_RANGE_INVALID);
			}
		}
		return new RegionObjective(new Region.Sphere(loc, rng), context.hasFlag('i'));
	}

	@Override
	protected void save(StorageKey key) {
		key.setString("location", Util.serializeLocString(location));
		if(range != 3) {
			key.setInt("range", range);
		}
	}

	protected static Objective load(StorageKey key) {
		Location location = Util.deserializeLocString(key.getString("location", ""));
		int range = 3;
		if(location == null) {
			return null;
		}
		range = key.getInt("range", 3);
		if(range < 1) {
			range = 3;
		}
		Region region = new Region.Sphere(location, range);
		
		return new RegionObjective(region, key.getBoolean("inverted", false));
	}

	//Custom methods

	public boolean checkLocation(Location loc) {
		if(loc.getWorld().getName().equalsIgnoreCase(location.getWorld().getName())) {
			return loc.distanceSquared(location) < range*range;
		} else {
			return false;
		}
	}
}