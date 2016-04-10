package me.ragan262.quester.objectives;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.QLocation;
import me.ragan262.quester.utils.SerUtils;
import org.bukkit.Location;

@QElement("DEATH")
public final class DeathObjective extends Objective {
	
	private final QLocation location;
	private final int amount;
	private final int range;
	
	public DeathObjective(final int amt, final QLocation loc, final int rng) {
		amount = amt;
		range = rng;
		location = loc;
	}
	
	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(final int progress) {
		final String locStr = location == null
				? "anywhere "
				: String.format("max %d blocks from %.1f %.1f %.1f("
						+ location.getWorldName() + ") ", range, location.getX(), location.getY(), location.getZ());
		return "Die " + locStr + String.valueOf(amount - progress) + "x.";
	}
	
	@Override
	protected String info() {
		final String locStr = location == null
				? "ANY"
				: String.format("%.1f %.1f %.1f(" + location.getWorldName() + ")", location.getX(), location.getY(), location.getZ());
		return "LOC: " + locStr + "; AMT: " + amount + "; RNG: " + range;
	}
	
	@Command(min = 1, max = 3, usage = "<amount> {[location]} [range]")
	public static Objective fromCommand(final QuesterCommandContext context)
			throws CommandException {
		QLocation loc = null;
		int rng = 5;
		final int amt = Integer.parseInt(context.getString(0));
		if(amt < 1) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_POSITIVE"));
		}
		if(context.length() > 1) {
			loc = SerUtils.getLoc(context.getPlayer(), context.getString(1));
			if(context.length() > 2) {
				rng = context.getInt(2);
				if(rng < 1) {
					throw new CommandException(context.getSenderLang().get("ERROR_CMD_RANGE_INVALID"));
				}
			}
		}
		return new DeathObjective(amt, loc, rng);
	}
	
	@Override
	protected void save(final StorageKey key) {
		if(location != null) {
			key.setString("location", SerUtils.serializeLocString(location));
		}
		if(amount != 1) {
			key.setInt("amount", amount);
		}
		if(range != 5) {
			key.setInt("range", range);
		}
	}
	
	protected static Objective load(final StorageKey key) {
		QLocation location;
		int amount, range;
		location = SerUtils.deserializeLocString(key.getString("location", ""));
		amount = key.getInt("amount", 1);
		if(amount < 1) {
			amount = 1;
		}
		range = key.getInt("range", 5);
		if(range < 1) {
			range = 5;
		}
		return new DeathObjective(amount, location, range);
	}
	
	// Custom methods
	
	public boolean checkDeath(final Location loc) {
		return location == null || loc.getWorld().getName().equalsIgnoreCase(location.getWorldName())
				&& loc.distance(location.getLocation()) < range;
	}
}
