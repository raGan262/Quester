package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.utils.Util;

public final class BreakObjective extends Objective {

	public static final String TYPE = "BREAK";
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
	public String getType() {
		return TYPE;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public byte getData() {
		return data;
	}
	
	public boolean checkHand(int itm) {
		return (inHand < 0 || inHand == itm);
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}

	@Override
	public boolean isComplete(Player player, int progress) {
		return progress >= amount;
	}
	
	@Override
	public String progress(int progress) {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%r", String.valueOf(amount - progress)).replaceAll("%t", String.valueOf(amount));
		}
		String datStr = data < 0 ? " " : " of given type(" + data + ") ";
		String hand = (inHand < 0) ? " " : (inHand == 0) ? "with empty hand " : "with " + Material.getMaterial(inHand).name().toLowerCase().replace('_', ' ') + " ";
		return "Break " + material.name().toLowerCase().replace('_', ' ') + datStr + hand + "- " + (amount - progress) + "x.";
	}
	
	@Override
	public String toString() {
		String dataStr = (data < 0 ? "" : ":" + data);
		return TYPE + ": " + material.name() + "["+material.getId() + dataStr + "]; AMT: " + amount + "; HND: " + inHand + coloredDesc();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		
		section.set("block", Util.serializeItem(material, data));
		if(amount > 1)
			section.set("amount", amount);
		if(inHand > 0)
			section.set("inhand", inHand);
	}
	
	public static Objective deser(ConfigurationSection section) {
		Material mat;
		int dat, amt;
		int hnd = -1;
		try {
			int[] itm = Util.parseItem(section.getString("block", ""));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
		} catch (QuesterException e) {
			return null;
		}
		amt = section.getInt("amount", 1);
		if(amt < 1) {
			return null;
		}
		try {
			hnd = Util.parseItem(section.getString("inhand", ""))[0];
		} catch (QuesterException ignore) {}
		return new BreakObjective(amt, mat, dat, hnd);
	}
}
