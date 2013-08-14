package com.gmail.molnardad.quester.objectives;

import org.bukkit.Material;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.SerUtils;

@QElement("COLLECT")
public final class CollectObjective extends Objective {
	
	private final Material material;
	private final short data;
	private final int amount;
	
	public CollectObjective(final int amt, final Material mat, final int dat) {
		amount = amt;
		material = mat;
		data = (short) dat;
	}
	
	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(final int progress) {
		final String datStr = data < 0 ? " " : " of given type(" + data + ") ";
		return "Collect " + material.name().toLowerCase().replace('_', ' ') + datStr + "- "
				+ (amount - progress) + "x.";
	}
	
	@Override
	protected String info() {
		final String dataStr = data < 0 ? "" : ":" + data;
		return material.name() + "[" + material.getId() + dataStr + "]; AMT: " + amount;
	}
	
	@QCommand(min = 2, max = 2, usage = "{<item>} <amount>")
	public static Objective fromCommand(final QCommandContext context) throws QCommandException {
		final int[] itm = SerUtils.parseItem(context.getString(0));
		final Material mat = Material.getMaterial(itm[0]);
		final int dat = itm[1];
		final int amt = Integer.parseInt(context.getString(1));
		if(amt < 1 || dat < -1) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_ITEM_NUMBERS"));
		}
		return new CollectObjective(amt, mat, dat);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("item", SerUtils.serializeItem(material, data));
		key.setInt("amount", amount);
	}
	
	protected static Objective load(final StorageKey key) {
		Material mat;
		int dat, amt;
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
		return new CollectObjective(amt, mat, dat);
	}
	
	// Custom methods
	
	public Material getMaterial() {
		return material;
	}
	
	public short getData() {
		return data;
	}
}
