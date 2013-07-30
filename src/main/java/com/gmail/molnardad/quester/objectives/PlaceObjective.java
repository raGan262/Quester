package com.gmail.molnardad.quester.objectives;

import static com.gmail.molnardad.quester.utils.Util.parseItem;

import org.bukkit.Material;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("PLACE")
public final class PlaceObjective extends Objective {
	
	private final Material material;
	private final byte data;
	private final int amount;
	
	public PlaceObjective(final int amt, final Material mat, final int dat) {
		amount = amt;
		material = mat;
		data = (byte) dat;
	}
	
	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(final int progress) {
		final String datStr = data < 0 ? " " : " (data " + data + ") ";
		return "Place " + material.name().toLowerCase().replace('_', ' ') + datStr + "- "
				+ (amount - progress) + "x.";
	}
	
	@Override
	protected String info() {
		final String dataStr = data < 0 ? "" : ":" + data;
		// return String.format("%s[%d%s]; AMT: %d ", material.name(), material.getId(), dataStr,
		// amount);
		return material.name() + "[" + material.getId() + dataStr + "]; AMT: " + amount;
	}
	
	@QCommand(min = 2, max = 2, usage = "{<item>} <amount>")
	public static Objective fromCommand(final QCommandContext context) throws QCommandException {
		final int[] itm = parseItem(context.getString(0));
		final Material mat = Material.getMaterial(itm[0]);
		final byte dat = (byte) itm[1];
		if(mat.getId() > 255) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_BLOCK_UNKNOWN);
		}
		final int amt = Integer.parseInt(context.getString(1));
		if(amt < 1 || dat < -1) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_ITEM_NUMBERS);
		}
		return new PlaceObjective(amt, mat, dat);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("block", Util.serializeItem(material, data));
		if(amount > 1) {
			key.setInt("amount", amount);
		}
	}
	
	protected static Objective load(final StorageKey key) {
		Material mat;
		int dat, amt = 1;
		try {
			final int[] itm = Util.parseItem(key.getString("block", ""));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
		}
		catch (final IllegalArgumentException e) {
			return null;
		}
		amt = key.getInt("amount", 1);
		if(amt < 1) {
			amt = 1;
		}
		return new PlaceObjective(amt, mat, dat);
	}
	
	// Custom methods
	
	public Material getMaterial() {
		return material;
	}
	
	public byte getData() {
		return data;
	}
}
