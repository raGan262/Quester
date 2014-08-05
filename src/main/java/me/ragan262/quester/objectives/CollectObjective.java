package me.ragan262.quester.objectives;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.SerUtils;
import org.bukkit.Material;

@QElement("COLLECT")
public final class CollectObjective extends Objective {
	
	private final Material material;
	private final short data;
	private final int amount;
	
	public CollectObjective(final int amt, final Material mat, final int dat) {
		amount = amt;
		material = mat;
		data = (short)dat;
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
	
	@Command(min = 2, max = 2, usage = "{<item>} <amount>")
	public static Objective fromCommand(final QuesterCommandContext context) throws CommandException {
		final int[] itm = SerUtils.parseItem(context.getString(0));
		final Material mat = Material.getMaterial(itm[0]);
		final int dat = itm[1];
		final int amt = Integer.parseInt(context.getString(1));
		if(amt < 1 || dat < -1) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_ITEM_NUMBERS"));
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
		catch(final IllegalArgumentException e) {
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
