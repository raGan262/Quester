package com.gmail.molnardad.quester.objectives;

import static com.gmail.molnardad.quester.utils.Util.parseItem;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.utils.Util;

@QElement("CRAFT")
public final class CraftObjective extends Objective {

	private final Material material;
	private final short data;
	private final int amount;
	
	public CraftObjective(int amt, Material mat, int dat) {
		material = mat;
		amount = amt;
		data = (short)dat;
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(int progress) {
		String datStr = data < 0 ? " " : " (data " + data + ") ";
		String pcs = (amount - progress) == 1 ? " piece of " : " pieces of ";
		String mat = material.getId() == 351 ? "dye" : material.name().toLowerCase();
		return "Craft " + (amount - progress) + pcs + mat + datStr + ".";
	}
	
	@Override
	protected String info() {
		String dataStr = (data < 0 ? "" : ":" + data);
		return material.name() + "["+material.getId() + dataStr + "]; AMT: " + amount;
	}
	
	@QCommand(
			min = 2,
			max = 2,
			usage = "{<item>} <amount>")
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
		int[] itm = parseItem(context.getString(0));
		Material mat = Material.getMaterial(itm[0]);
		int dat = itm[1];
		int amt = Integer.parseInt(context.getString(1));
		if(amt < 1 || dat < -1) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_ITEM_NUMBERS);
		}
		return new CraftObjective(amt, mat, dat);
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		
		section.set("item", Util.serializeItem(material, data));
		if(amount > 1)
			section.set("amount", amount);
	}
	
	public static Objective deser(ConfigurationSection section) {
		Material mat;
		int dat, amt = 1;
		try {
			int[] itm = Util.parseItem(section.getString("item", ""));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
			} catch (IllegalArgumentException e) {
				return null;
		}
		if(section.isInt("amount")) {
			amt = section.getInt("amount");
			if(amt < 1)
				amt = 1;
		}
		return new CraftObjective(amt, mat, dat);
	}
	
	// Custom methods
	
	public boolean check(ItemStack item) {
		if(item.getTypeId() != material.getId())
			return false;
		if(item.getDurability() != data && data >= 0)
			return false;
		return true;
	}
}
