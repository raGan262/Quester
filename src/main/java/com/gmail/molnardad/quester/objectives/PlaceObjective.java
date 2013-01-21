package com.gmail.molnardad.quester.objectives;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.utils.Util;

@QElement("PLACE")
public final class PlaceObjective extends Objective {

	private final Material material;
	private final byte data;
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

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("block", Util.serializeItem(material, data));
		if(amount > 1)
			section.set("amount", amount);
	}
	
	public static Objective deser(ConfigurationSection section) {
		Material mat;
		int dat, amt = 1;
		try {
			int[] itm = Util.parseItem(section.getString("block", ""));
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
		return new PlaceObjective(amt, mat, dat);
	}
	
	//Custom methods
	
	public Material getMaterial() {
		return material;
	}
	
	public byte getData() {
		return data;
	}
}
