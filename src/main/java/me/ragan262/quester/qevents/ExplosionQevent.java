package me.ragan262.quester.qevents;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.QLocation;
import me.ragan262.quester.utils.SerUtils;
import me.ragan262.quester.utils.Util;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@QElement("EXPLOSION")
public final class ExplosionQevent extends Qevent {
	
	private final QLocation location;
	private final boolean damage;
	private final int range;
	
	public ExplosionQevent(final QLocation loc, final int rng, final boolean dmg) {
		location = loc;
		damage = dmg;
		range = rng;
	}
	
	@Override
	public String info() {
		String locStr = "PLAYER";
		if(location != null) {
			locStr = SerUtils.displayLocation(location);
		}
		return locStr + "; RNG: " + range + "; DMG: " + damage;
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		Location loc;
		if(location == null) {
			loc = Util.move(player.getLocation(), range);
		}
		else {
			loc = Util.move(location.getLocation(), range);
		}
		
		if(damage) {
			loc.getWorld().createExplosion(loc, 4F);
		}
		else {
			loc.getWorld().createExplosion(loc, 0F);
		}
	}
	
	@Command(min = 1, max = 2, usage = "{<location>} [range] (-d)")
	public static Qevent fromCommand(final QuesterCommandContext context) {
		final QLocation loc = SerUtils.getLoc(context.getPlayer(), context.getString(0));
		int range = 0;
		if(context.length() > 1) {
			range = context.getInt(1);
		}
		return new ExplosionQevent(loc, range, context.hasFlag('d'));
	}
	
	@Override
	protected void save(final StorageKey key) {
		if(damage) {
			key.setBoolean("damage", true);
		}
		if(location != null) {
			key.setString("location", SerUtils.serializeLocString(location));
		}
		if(range != 0) {
			key.setInt("range", range);
		}
	}
	
	protected static Qevent load(final StorageKey key) {
		final QLocation loc = SerUtils.deserializeLocString(key.getString("location", ""));
		int rng = key.getInt("range", 0);
		if(rng < 0) {
			rng = 0;
		}
		
		return new ExplosionQevent(loc, rng, key.getBoolean("damage", false));
	}
}
