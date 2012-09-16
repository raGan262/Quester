package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.utils.Util;

public final class CollectObjective extends Objective {

	private final String TYPE = "COLLECT";
	private final Material material;
	private final short data;
	private final int amount;
	
	public CollectObjective(int amt, Material mat, int dat) {
		amount = amt;
		material = mat;
		data = (short) dat;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public short getData() {
		return data;
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
		return "Collect " + material.name().toLowerCase().replace('_', ' ') + datStr + "- " + (amount - progress) + "x.";
	}
	
	@Override
	public String toString() {
		String dataStr = (data < 0 ? "" : ":" + data);
		return TYPE + ": " + material.name() + "["+material.getId() + dataStr + "]; AMT: " + amount + coloredDesc() + stringQevents();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		
		section.set("item", Util.serializeItem(material, data));
		section.set("amount", amount);
	}
	
	public static Objective deser(ConfigurationSection section) {
		Material mat;
		int dat, amt;
		if(section.isString("item")) {
			try {
			int[] itm = Util.parseItem(section.getString("item"));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
			} catch (QuesterException e) {
				return null;
			}
		} else 
			return null;
		if(section.isInt("amount")) {
			amt = section.getInt("amount");
			if(amt < 1)
				return null;
		} else 
			return null;
		return new CollectObjective(amt, mat, dat);
	}
}
