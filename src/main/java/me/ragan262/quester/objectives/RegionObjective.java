package me.ragan262.quester.objectives;

import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Region;

import org.bukkit.Location;

@QElement("REGION")
public final class RegionObjective extends Objective {
	
	private final Region region;
	private final boolean inverted;
	
	public RegionObjective(final Region region, final boolean inverted) {
		this.region = region;
		this.inverted = inverted;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(final int progress) {
		final String type = inverted ? "out of" : "into";
		return "Go " + type + " the region " + region.toString() + ".";
	}
	
	@Override
	protected String info() {
		return region.toString();
	}
	
	@QCommand(min = 1, max = 1, usage = "{<region>} (-i)")
	public static Objective fromCommand(final QCommandContext context) throws QCommandException {
		final Region region = Region.fromString(context.getPlayer(), context.getString(0));
		if(region == null) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_REGION_INVALID"));
		}
		return new RegionObjective(region, context.hasFlag('i'));
	}
	
	@Override
	protected void save(final StorageKey key) {
		region.serialize(key.getSubKey("region"));
		if(inverted) {
			key.setBoolean("inverted", inverted);
		}
	}
	
	protected static Objective load(final StorageKey key) {
		final Region region = Region.deserialize(key.getSubKey("region"));
		
		if(region == null) {
			return null;
		}
		return new RegionObjective(region, key.getBoolean("inverted", false));
	}
	
	// Custom methods
	
	public boolean checkLocation(final Location loc) {
		return region.isWithin(loc);
	}
}
