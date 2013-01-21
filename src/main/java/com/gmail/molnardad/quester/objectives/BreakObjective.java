package com.gmail.molnardad.quester.objectives;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
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

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {		
		section.set("block", Util.serializeItem(material, data));
		if(amount > 1)
			section.set("amount", amount);
		if(inHand > 0)
			section.set("inhand", inHand);
	}
	
	public static Objective deser(ConfigurationSection section) {
		Material mat;
		int dat, amt = 1;
		int hnd = -1;
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
				return null;
		} else 
			return null;
		try {
				hnd = Util.parseItem(section.getString("inhand", ""))[0];
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
