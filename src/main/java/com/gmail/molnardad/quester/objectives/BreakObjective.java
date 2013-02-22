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

@QElement("BREAK")
public final class BreakObjective extends Objective {

	private final Material material;
	private final byte data;
	private final int amount;
	private final int inHand;
	
	public BreakObjective(int amt, Material mat, int dat, int hnd) {
		amount = amt;
		material = mat;
		data = (byte) dat;
		inHand = hnd;
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(int progress) {
		String datStr = data < 0 ? " " : " of given type(" + data + ") ";
		String hand = (inHand < 0) ? " " : (inHand == 0) ? "with empty hand " : "with " + Material.getMaterial(inHand).name().toLowerCase().replace('_', ' ') + " ";
		return "Break " + material.name().toLowerCase().replace('_', ' ') + datStr + hand + "- " + (amount - progress) + "x.";
	}
	
	@Override
	protected String info() {
		String dataStr = (data < 0 ? "" : ":" + data);
		return material.name() + "["+material.getId() + dataStr + "]; AMT: " + amount + "; HND: " + inHand;
	}
	
	@QCommand(
			min = 2,
			max = 3,
			usage = "{<item>} <amount> [hand]")
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
		int hnd = -1;
		int[] itm = parseItem(context.getString(0));
		Material mat = Material.getMaterial(itm[0]);
		byte dat = (byte)itm[1];
		if(mat.getId() > 255) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_BLOCK_UNKNOWN);
		}
		int amt = Integer.parseInt(context.getString(1));
		if(amt < 1 || dat < -1) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_ITEM_NUMBERS);
		}
		if(context.length() > 2) {
			itm = parseItem(context.getString(2));
			hnd = itm[0];
		}
		return new BreakObjective(amt, mat, dat, hnd);
	}

	@Override
	protected void save(StorageKey key) {		
		key.setString("block", Util.serializeItem(material, data));
		if(amount > 1) {
			key.setInt("amount", amount);
		}
		if(inHand > 0) {
			key.setInt("inhand", inHand);
		}
	}
	
	protected static Objective load(StorageKey key) {
		Material mat;
		int dat, amt;
		int hnd = -1;
		try {
			int[] itm = Util.parseItem(key.getString("block", ""));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
		} catch (IllegalArgumentException e) {
				return null;
		}
		amt = key.getInt("amount", 1);
		if(amt < 1) {
			amt = 1;
		}
		try {
				hnd = Util.parseItem(key.getString("inhand", ""))[0];
			} catch (IllegalArgumentException e) {
		}
		return new BreakObjective(amt, mat, dat, hnd);
	}
	
	// Custom methods
	
	public Material getMaterial() {
		return material;
	}
	
	public byte getData() {
		return data;
	}
	
	public boolean checkHand(int itm) {
		return (inHand < 0 || inHand == itm);
	}
}
