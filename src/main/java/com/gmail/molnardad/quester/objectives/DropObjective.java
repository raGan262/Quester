package com.gmail.molnardad.quester.objectives;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.SerUtils;
import com.gmail.molnardad.quester.utils.Util;

@QElement("DROP")
public final class DropObjective extends Objective {
	
	private final Material material;
	private final short data;
	private final int amount;
	private final boolean questItem;
	private final Location location;
	private final double range;
	private final double range2;
	
	public DropObjective(final int amt, final Material mat, final int dat, final Location loc, final double rng, final boolean quest) {
		amount = amt;
		material = mat;
		data = (short) dat;
		questItem = quest;
		location = loc;
		range = rng;
		range2 = rng * rng;
	}
	
	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(final int progress) {
		final String datStr = data < 0 ? "" : " (data" + data + ")";
		final String spec = questItem ? " special" : "";
		final String locStr = location == null ? "" : " at " + SerUtils.displayLocation(location);
		return "Drop " + spec + material.name().toLowerCase().replace('_', ' ') + datStr + locStr
				+ " - " + progress + "/" + amount + ".";
	}
	
	@Override
	protected String info() {
		final String dataStr = data < 0 ? "" : ":" + data;
		final String locStr =
				location == null ? "" : "; LOC: " + SerUtils.displayLocation(location) + "; RNG: "
						+ range;
		final String flags = questItem ? " (-q)" : "";
		return material.name() + "[" + material.getId() + dataStr + "]; AMT: " + amount + locStr
				+ flags;
	}
	
	@QCommand(min = 2, max = 4, usage = "{<item>} <amount> {[location]} [range] (-q)")
	public static Objective fromCommand(final QCommandContext context) throws QCommandException {
		final int[] itm = SerUtils.parseItem(context.getString(0));
		final Material mat = Material.getMaterial(itm[0]);
		final int dat = itm[1];
		final int amt = context.getInt(1);
		if(amt < 1 || dat < -1) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_ITEM_NUMBERS"));
		}
		Location loc = null;
		double rng = 2.0;
		if(context.length() > 2) {
			loc = SerUtils.getLoc(context.getPlayer(), context.getString(2));
			if(context.length() > 3) {
				rng = context.getDouble(3);
				if(rng < 0) {
					throw new QCommandException(context.getSenderLang().get("ERROR_CMD_RANGE_INVALID"));
				}
			}
		}
		return new DropObjective(amt, mat, dat, loc, rng, context.hasFlag('q'));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("item", SerUtils.serializeItem(material, data));
		key.setInt("amount", amount);
		if(location != null) {
			key.setString("location", SerUtils.serializeLocString(location));
			key.setDouble("range", range);
		}
		if(questItem) {
			key.setBoolean("questitem", questItem);
		}
	}
	
	protected static Objective load(final StorageKey key) {
		Material mat;
		int dat, amt;
		Location loc = null;
		double rng = 2.0;
		try {
			final int[] itm = SerUtils.parseItem(key.getString("item", ""));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
		}
		catch (final IllegalArgumentException e) {
			return null;
		}
		amt = key.getInt("amount", 0);
		if(amt < 1) {
			return null;
		}
		loc = SerUtils.deserializeLocString(key.getString("location", ""));
		if(loc != null) {
			rng = key.getDouble("range", 2.0);
			if(rng < 0) {
				rng = 2.0;
			}
		}
		return new DropObjective(amt, mat, dat, loc, rng, key.getBoolean("questitem", false));
	}
	
	// Custom methods
	
	public boolean isMatching(final ItemStack item) {
		if(questItem != Util.isQuestItem(item)) {
			return false;
		}
		return item.getType() == material && (data < 0 || item.getDurability() == data);
	}
	
	public boolean isMatching(final Location loc) {
		if(location == null) {
			return true;
		}
		if(loc == null) {
			return false;
		}
		return location.distanceSquared(loc) <= range2;
	}
}
