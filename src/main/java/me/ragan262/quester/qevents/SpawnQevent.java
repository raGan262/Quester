package me.ragan262.quester.qevents;

import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.SerUtils;
import me.ragan262.quester.utils.Util;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@QElement("SPAWN")
public final class SpawnQevent extends Qevent {
	
	private final Location location;
	private final EntityType entity;
	private final int range;
	private final int amount;
	
	public SpawnQevent(final Location loc, final int rng, final EntityType ent, final int amt) {
		location = loc;
		range = rng;
		entity = ent;
		amount = amt;
	}
	
	@Override
	public String info() {
		String locStr = "PLAYER";
		if(location != null) {
			locStr = SerUtils.displayLocation(location);
		}
		return entity.getName() + "; AMT: " + amount + "; LOC: " + locStr + "; RNG: " + range;
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		Location temp;
		if(location == null) {
			temp = player.getLocation();
		}
		else {
			temp = location;
		}
		for(int i = 0; i < amount; i++) {
			temp.getWorld().spawnEntity(Util.move(temp, range), entity);
		}
	}
	
	@QCommand(min = 3, max = 4, usage = "{<entity>} <amount> {<location>} [range]")
	public static Qevent fromCommand(final QCommandContext context) throws QCommandException {
		final EntityType ent = SerUtils.parseEntity(context.getString(0));
		final int amt = context.getInt(1);
		final Location loc = SerUtils.getLoc(context.getSender(), context.getString(2));
		int rng = 0;
		if(amt < 1) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_POSITIVE"));
		}
		if(context.length() > 3) {
			rng = context.getInt(3);
			if(rng < 0) {
				throw new QCommandException(context.getSenderLang().get("ERROR_CMD_RANGE_INVALID"));
			}
		}
		return new SpawnQevent(loc, rng, ent, amt);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setInt("entity", entity.getTypeId());
		if(amount != 1) {
			key.setInt("amount", amount);
		}
		if(location != null) {
			key.setString("location", SerUtils.serializeLocString(location));
		}
		if(range != 0) {
			key.setInt("range", range);
		}
	}
	
	protected static Qevent load(final StorageKey key) {
		EntityType ent = null;
		try {
			ent = SerUtils.parseEntity(key.getString("entity", ""));
		}
		catch (final Exception e) {
			return null;
		}
		
		final Location loc = SerUtils.deserializeLocString(key.getString("location", ""));
		
		int rng = key.getInt("range", 0);
		if(rng < 0) {
			rng = 0;
		}
		int amt = key.getInt("amount", 1);
		if(amt < 1) {
			amt = 1;
		}
		
		return new SpawnQevent(loc, rng, ent, amt);
	}
}
