package com.gmail.molnardad.quester.qevents;

import static com.gmail.molnardad.quester.utils.Util.parseEntity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("SPAWN")
public final class SpawnQevent extends Qevent {

	private final Location location;
	private final EntityType entity;
	private final int range;
	private final int amount;
	
	public SpawnQevent(Location loc, int rng, EntityType ent, int amt) {
		this.location = loc;
		this.range = rng;
		this.entity = ent;
		this.amount = amt;
	}
	
	@Override
	public String info() {
		String locStr = "PLAYER";
		if(location != null) {
			locStr = Util.displayLocation(location);
		}
		return entity.getName() + "; AMT: " + amount + "; LOC: " + locStr + "; RNG: " + range;
	}

	@Override
	protected void run(Player player, Quester plugin) {
		Location temp;
		if(location == null)
			temp = player.getLocation();
		else
			temp = location;
		for(int i = 0; i < amount; i++) {
			temp.getWorld().spawnEntity(Util.move(temp, range), entity);
		}
	}

	@QCommand(
			min = 3,
			max = 4,
			usage = "{<entity>} <amount> {<location>} [range]")
	public static Qevent fromCommand(QCommandContext context) throws QCommandException {
		EntityType ent = parseEntity(context.getString(0));
		int amt = context.getInt(1);
		Location loc = Util.getLoc(context.getPlayer(), context.getString(2));
		int rng = 0;
		if(amt < 1) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_POSITIVE);
		}
		if(context.length() > 3) {
			rng = context.getInt(3);
			if(rng < 0) {
				throw new QCommandException(context.getSenderLang().ERROR_CMD_RANGE_INVALID);
			}
		}
		return new SpawnQevent(loc, rng, ent, amt);
	}

	@Override
	public void save(StorageKey key) {
		if(amount != 1) {
			key.setInt("amount", amount);
		}
		key.setInt("entity", entity.getTypeId());
		key.setString("location", Util.serializeLocString(location));
		if(range != 0) {
			key.setInt("range", range);
		}
	}
	
	public static SpawnQevent load(StorageKey key) {
		int rng = 0, amt = 1;
		EntityType ent = null;
		Location loc = null;
		try {
			loc = Util.deserializeLocString(key.getString("location", ""));
			rng = key.getInt("range", 0);
			if(rng < 0) {
				rng = 0;
			}
			amt = key.getInt("amount", 1);
			if(amt < 1) {
				amt = 1;
			}
			try {
				ent = Util.parseEntity(key.getString("entity", ""));
			} catch (Exception ignore) {}
			if(ent == null) {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
		
		return new SpawnQevent(loc, rng, ent, amt);
	}
}
