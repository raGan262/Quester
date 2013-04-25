package com.gmail.molnardad.quester.objectives;

import static com.gmail.molnardad.quester.utils.Util.parseItem;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("DROP")
public final class DropObjective extends Objective {

	/**
	 * @uml.property  name="material"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private final Material material;
	/**
	 * @uml.property  name="data"
	 */
	private final short data;
	/**
	 * @uml.property  name="amount"
	 */
	private final int amount;
	/**
	 * @uml.property  name="questItem"
	 */
	private final boolean questItem;
	/**
	 * @uml.property  name="location"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private final Location location;
	/**
	 * @uml.property  name="range"
	 */
	private final double range;
	/**
	 * @uml.property  name="range2"
	 */
	private final double range2;
	
	public DropObjective(int amt, Material mat, int dat, Location loc, double rng, boolean quest) {
		this.amount = amt;
		this.material = mat;
		this.data = (short) dat;
		this.questItem = quest;
		this.location = loc;
		this.range = rng;
		this.range2 = rng*rng;
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(int progress) {
		String datStr = data < 0 ? "" : " (data" + data + ")";
		String spec = questItem ? " special" : "";
		String locStr = location == null ? "" : " at " + Util.displayLocation(location);
		return "Drop " + spec + material.name().toLowerCase().replace('_', ' ') + datStr + locStr +  " - " + progress + "/" + amount + ".";
	}
	
	@Override
	protected String info() {
		String dataStr = (data < 0 ? "" : ":" + data);
		String locStr = location == null ? "" : "; LOC: " + Util.displayLocation(location) + "; RNG: " + range;
		String flags = questItem ? " (-q)" : "";
		return material.name() + "["+material.getId() + dataStr + "]; AMT: " + amount + locStr + flags;
	}
	
	@QCommand(
			min = 2,
			max = 4,
			usage = "{<item>} <amount> {[location]} [range] (-q)")
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
		int[] itm = parseItem(context.getString(0));
		Material mat = Material.getMaterial(itm[0]);
		int dat = itm[1];
		int amt = context.getInt(1);
		if(amt < 1 || dat < -1) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_ITEM_NUMBERS);
		}
		Location loc = null;
		double rng = 2.0;
		if(context.length() > 2) {
			loc = Util.getLoc(context.getPlayer(), context.getString(2));
			if(context.length() > 3) {
				rng = context.getDouble(3);
				if(rng < 0) {
					throw new QCommandException(context.getSenderLang().ERROR_CMD_RANGE_INVALID);
				}
			}
		}
		return new DropObjective(amt, mat, dat, loc, rng, context.hasFlag('q'));
	}

	@Override
	protected void save(StorageKey key) {
		key.setString("item", Util.serializeItem(material, data));
		key.setInt("amount", amount);
		if(location != null) {
			key.setString("location", Util.serializeLocString(location));
			key.setDouble("range", range);
		}
		if(questItem) {
			key.setBoolean("questitem", questItem);
		}
	}
	
	protected static Objective load(StorageKey key) {
		Material mat;
		int dat, amt;
		Location loc = null;
		double rng = 2.0;
		try {
			int[] itm = Util.parseItem(key.getString("item", ""));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
		} catch (IllegalArgumentException e) {
				return null;
		}
		amt = key.getInt("amount", 0);
		if(amt < 1) {
			return null;
		}
		loc = Util.deserializeLocString(key.getString("location", ""));
		if(loc != null) {
			rng = key.getDouble("range", 2.0);
			if(rng < 0) {
				rng = 2.0;
			}
		}
		return new DropObjective(amt, mat, dat, loc, rng, key.getBoolean("questitem", false));
	}
	
	// Custom methods
	
	public boolean isMatching(ItemStack item) {
		if(questItem != Util.isQuestItem(item)) {
			return false;
		}
		return item.getType() == material && (data < 0 || item.getDurability() == data);
	}
	
	public boolean isMatching(Location loc) {
		if(location == null) {
			return true;
		}
		if(loc == null) {
			return false;
		}
		return location.distanceSquared(loc) <= range2;
	}
}
