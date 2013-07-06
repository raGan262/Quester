package com.gmail.molnardad.quester.objectives;

import org.bukkit.Location;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Region;
import com.gmail.molnardad.quester.utils.Util;
import com.gmail.molnardad.quester.utils.Region.Sphere;

@QElement("REGION")
public final class RegionObjective extends Objective {

	private final Region region;
	private final boolean inverted;
	
	public RegionObjective(Region region, boolean inverted) {
		this.region = region;
		this.inverted = inverted;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(int progress) {
		String type = inverted ? "out of" : "into";
		return "Go " + type + " the region " + region.toString() + ".";
	}
	
	@Override
	protected String info() {
		return region.toString();
	}
	
	@QCommand(
			min = 1,
			max = 1,
			usage = "{<region>} (-i)")
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
		Region region = Region.fromString(context.getPlayer(), context.getString(0));
		if(region == null){
			throw new QCommandException(context.getSenderLang().ERROR_CMD_REGION_INVALID);
		}
		return new RegionObjective(region, context.hasFlag('i'));
	}

	@Override
	protected void save(StorageKey key) {
		region.serialize(key.getSubKey("region"));
		if(inverted) {
			key.setBoolean("inverted", inverted);
		}
	}
	
	protected static Objective load(StorageKey key) {
		Region region = Region.deserialize(key.getSubKey("region"));
		
		if(region == null) {
			Location location = Util.deserializeLocString(key.getString("location", ""));
			int range = 3;
			if(location == null) {
				return null;
			}
			range = key.getInt("range", 3);
			if(range < 1) {
				range = 3;
			}
			region = new Sphere(location, range);
		}
		return new RegionObjective(region, key.getBoolean("inverted", false));
	}
	
	//Custom methods

	public boolean checkLocation(Location loc) {
		return region.isWithin(loc);
	}
}
