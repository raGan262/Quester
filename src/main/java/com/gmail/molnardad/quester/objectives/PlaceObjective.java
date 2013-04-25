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

	/**
	 * @uml.property  name="material"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private final Material material;
	/**
	 * @uml.property  name="data"
	 */
	private final byte data;
	/**
	 * @uml.property  name="amount"
	 */
	private final int amount;
	
	public PlaceObjective(int amt, Material mat, int dat) {
		amount = amt;
		material = mat;
		data = (byte)dat;
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}

	@Override
	protected String show(int progress) {
		String datStr = data < 0 ? " " : " (data " + data + ") ";
		return "Place " + material.name().toLowerCase().replace('_', ' ') + datStr + "- " + (amount - progress) + "x.";
	}
	
	@Override
	protected String info() {
		String dataStr = (data < 0 ? "" : ":" + data);
		//return String.format("%s[%d%s]; AMT: %d ", material.name(), material.getId(), dataStr, amount);
		return material.name() + "["+material.getId() + dataStr + "]; AMT: " + amount;
	}
	
	@QCommand(
			min = 2,
			max = 2,
			usage = "{<item>} <amount>")
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
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
		return new PlaceObjective(amt, mat, dat);
	}

	@Override
	protected void save(StorageKey key) {
		key.setString("block", Util.serializeItem(material, data));
		if(amount > 1) {
			key.setInt("amount", amount);
		}
	}
	
	protected static Objective load(StorageKey key) {
		Material mat;
		int dat, amt = 1;
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
		return new PlaceObjective(amt, mat, dat);
	}
	
	//Custom methods
	
	/**
	 * @return
	 * @uml.property  name="material"
	 */
	public Material getMaterial() {
		return material;
	}
	
	/**
	 * @return
	 * @uml.property  name="data"
	 */
	public byte getData() {
		return data;
	}
}
