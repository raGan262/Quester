package me.ragan262.quester.objectives;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.SerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;

@QElement("BREAK")
public final class BreakObjective extends Objective {
	
	private final Material material;
	private final byte data;
	private final int amount;
	private final int inHand;
	
	public BreakObjective(final int amt, final Material mat, final int dat, final int hnd) {
		amount = amt;
		material = mat;
		data = (byte)dat;
		inHand = hnd;
	}
	
	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(final int progress) {
		final String matStr = material == null ? "any block"
				: material.name().toLowerCase().replace('_', ' ');
		final String datStr = data < 0 ? "" : " of given type(" + data + ")";
		final String hand = inHand < 0 ? " " : inHand == 0 ? "with empty hand " : "with "
				+ Material.getMaterial(inHand).name().toLowerCase().replace('_', ' ') + " ";
		return "Break " + matStr + datStr + hand + "- " + (amount - progress) + "x.";
	}
	
	@Override
	protected String info() {
		final String dataStr = data < 0 ? "" : ":" + data;
		final String matStr = material == null ? "ANY" : material.name() + "[" + material.getId()
				+ dataStr + "]";
		return matStr + "; AMT: " + amount + "; HND: " + inHand;
	}
	
	@Command(min = 2, max = 3, usage = "{<block>} <amount> [hand]")
	public static Objective fromCommand(final QuesterCommandContext context) throws CommandException {
		int hnd = -1;
		int[] itm;
		Material mat = null;
		byte dat = -1;
		if(!context.getString(0).equalsIgnoreCase("ANY")) {
			itm = SerUtils.parseItem(context.getString(0));
			mat = Material.getMaterial(itm[0]);
			dat = (byte)itm[1];
			if(mat.getId() > 255) {
				throw new CommandException(context.getSenderLang().get("ERROR_CMD_BLOCK_UNKNOWN"));
			}
		}
		
		final int amt = Integer.parseInt(context.getString(1));
		if(amt < 1 || dat < -1) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_ITEM_NUMBERS"));
		}
		if(context.length() > 2) {
			itm = SerUtils.parseItem(context.getString(2));
			hnd = itm[0];
		}
		return new BreakObjective(amt, mat, dat, hnd);
	}
	
	@Override
	protected void save(final StorageKey key) {
		if(material != null) {
			key.setString("block", SerUtils.serializeItem(material, data));
		}
		if(amount > 1) {
			key.setInt("amount", amount);
		}
		if(inHand > 0) {
			key.setInt("inhand", inHand);
		}
	}
	
	protected static Objective load(final StorageKey key) {
		Material mat = null;
		int dat = -1, amt;
		int hnd = -1;
		final String blockString = key.getString("block");
		if(blockString != null) {
			try {
				final int[] itm = SerUtils.parseItem(blockString);
				mat = Material.getMaterial(itm[0]);
				dat = itm[1];
			}
			catch(final IllegalArgumentException e) {
				return null;
			}
		}
		amt = key.getInt("amount", 1);
		if(amt < 1) {
			amt = 1;
		}
		try {
			hnd = SerUtils.parseItem(key.getString("inhand", ""))[0];
		}
		catch(final IllegalArgumentException ignored) {}
		return new BreakObjective(amt, mat, dat, hnd);
	}
	
	// Custom methods
	
	public boolean checkBlock(final Block block) {
		if(material == null) {
			return true;
		}
		if(data >= 0 && block.getData() != data) {
			return false;
		}
		// special case when mining redstone
		if(material == Material.REDSTONE_ORE) {
			return block.getType() == Material.REDSTONE_ORE
					|| block.getType() == Material.GLOWING_REDSTONE_ORE;
		}
		return material == block.getType();
	}
	
	public boolean checkHand(final int itm) {
		return inHand < 0 || inHand == itm;
	}
}
