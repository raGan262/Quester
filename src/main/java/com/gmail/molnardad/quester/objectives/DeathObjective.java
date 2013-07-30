package com.gmail.molnardad.quester.objectives;

import static com.gmail.molnardad.quester.utils.Util.getLoc;

import org.bukkit.Location;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("DEATH")
public final class DeathObjective extends Objective {
	
	private final Location location;
	private final int amount;
	private final int range;
	
	public DeathObjective(final int amt, final Location loc, final int rng) {
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
		final String locStr =
				location == null ? "anywhere " : String.format("max %d blocks from %.1f %.1f %.1f("
						+ location.getWorld().getName() + ") ", range, location.getX(),
						location.getY(), location.getZ());
		return "Die " + locStr + String.valueOf(amount - progress) + "x.";
	}
	
	@Override
	protected String info() {
		final String locStr =
				location == null ? "ANY" : String.format("%.1f %.1f %.1f("
						+ location.getWorld().getName() + ")", location.getX(), location.getY(),
						location.getZ());
		return "LOC: " + locStr + "; AMT: " + amount + "; RNG: " + range;
	}
	
	@QCommand(min = 1, max = 3, usage = "<amount> {[location]} [range]")
	public static Objective fromCommand(final QCommandContext context) throws QCommandException {
		Location loc = null;
		int rng = 5;
		final int amt = Integer.parseInt(context.getString(0));
		if(amt < 1) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_POSITIVE);
		}
		if(context.length() > 1) {
			loc = getLoc(context.getPlayer(), context.getString(1));
			if(context.length() > 2) {
				rng = context.getInt(2);
				if(rng < 1) {
					throw new QCommandException(context.getSenderLang().ERROR_CMD_RANGE_INVALID);
				}
			}
		}
		return new DeathObjective(amt, loc, rng);
	}
	
	@Override
	protected void save(final StorageKey key) {
		if(location != null) {
			key.setString("location", Util.serializeLocString(location));
		}
		if(amount != 1) {
			key.setInt("amount", amount);
		}
		if(range != 5) {
			key.setInt("range", range);
		}
	}
	
	protected static Objective load(final StorageKey key) {
		Location loc = null;
		int amt = 1, rng = 5;
		loc = Util.deserializeLocString(key.getString("location", ""));
		amt = key.getInt("amount", 1);
		if(amt < 1) {
			amt = 1;
		}
		rng = key.getInt("range", 5);
		if(rng < 1) {
			rng = 5;
		}
		return new DeathObjective(amt, loc, rng);
	}
	
	// Custom methods
	
	public boolean checkDeath(final Location loc) {
		if(location == null) {
			return true;
		}
		if(loc.getWorld().getName().equals(location.getWorld().getName())) {
			return loc.distance(location) < range;
		}
		return false;
	}
}
