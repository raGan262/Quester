package com.gmail.molnardad.quester.objectives;

import static com.gmail.molnardad.quester.utils.Util.getLoc;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.utils.Util;

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
		if(context.length() > 4){
			rng = context.getInt(1);
			if(rng < 1) {
				throw new QCommandException(context.getSenderLang().ERROR_CMD_RANGE_INVALID);
			}
		}
		return new LocObjective(loc, rng);
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("location", Util.serializeLocString(location));
		if(range != 5)
			section.set("range", range);
	}
	
	public static Objective deser(ConfigurationSection section) {
		Location loc = null;
		int rng = 3;
		loc = Util.deserializeLocString(section.getString("location", ""));
		if(loc == null)
			return null;
		if(section.isInt("range"))
			rng = section.getInt("range");
		if(rng < 1)
			rng = 3;
		return new LocObjective(loc, rng);
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
