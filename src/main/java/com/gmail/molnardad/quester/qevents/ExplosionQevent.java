package com.gmail.molnardad.quester.qevents;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("EXPLOSION")
public final class ExplosionQevent extends Qevent {

	private final Location location;
	private final boolean damage;
	private final int range;
	
	public ExplosionQevent(Location loc, int rng, boolean dmg) {
		this.location = loc;
		this.damage = dmg;
		this.range = rng;
	}
	
	@Override
	public String info() {
		String locStr = "PLAYER";
		if(location != null) {
			locStr = Util.displayLocation(location);
		}
		return locStr + "; RNG: " + range + "; DMG: " + damage;
	}

	@Override
	protected void run(Player player, Quester plugin) {
		Location loc;
		if(location == null)
			loc = Util.move(player.getLocation(), range);
		else
			loc = Util.move(location, range);
		
		if(damage)
			loc.getWorld().createExplosion(loc, 4F);
		else
			loc.getWorld().createExplosion(loc, 0F);
	}

	@QCommand(
			min = 1,
			max = 2,
			usage = "{<location>} [range] (-d)")
	public static Qevent fromCommand(QCommandContext context) {
		Location loc = Util.getLoc(context.getPlayer(), context.getString(0));
		int range = 0;
		if(context.length() > 1) {
			range = context.getInt(1);
		}
		return new ExplosionQevent(loc, range, context.hasFlag('d'));
	}

	@Override
	protected void save(StorageKey key) {
		if(damage) {
			key.setBoolean("damage", damage);
		}
		key.setString("location", Util.serializeLocString(location));
		if(range != 0) {
			key.setInt("range", range);
		}
	}
	
	protected static ExplosionQevent load(StorageKey key) {
		int rng = 0;
		boolean dmg = false;
		Location loc = null;
		try {
			loc = Util.deserializeLocString(key.getString("location"));
			rng = key.getInt("range", 0);
			if(rng < 0) {
				rng = 0;
			}
			dmg = key.getBoolean("damage", false);
		} catch (Exception e) {
			return null;
		}
		
		return new ExplosionQevent(loc, rng, dmg);
	}
}
