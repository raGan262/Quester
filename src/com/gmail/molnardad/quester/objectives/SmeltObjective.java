package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.utils.Util;

public final class SmeltObjective extends Objective {

	public static final String TYPE = "SMELT";
	private final Material material;
	private final short data;
	private final int amount;
	
	public SmeltObjective(int amt, Material mat, int dat) {
		material = mat;
		amount = amt;
		data = (short)dat;
	}
	
	@Override
	public String getType() {
		return TYPE;
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
		String datStr = data < 0 ? " " : " (data " + data + ") ";
		String pcs = (amount - progress) == 1 ? " piece of " : " pieces of ";
		String mat = material.getId() == 351 ? "dye" : material.name().toLowerCase();
		return "Smelt " + (amount - progress) + pcs + mat + datStr + ".";
	}
	
	public String toString() {
		String dataStr = (data < 0 ? "" : ":" + data);
		return TYPE + ": " + material.name() + "["+material.getId() + dataStr + "]; AMT: " + amount + coloredDesc() + stringQevents();
	}
	
	public boolean check(ItemStack item) {
		if(item.getTypeId() != material.getId())
			return false;
		if(item.getDurability() != data && data >= 0)
			return false;
		return true;
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		
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
			} catch (QuesterException e) {
				return null;
		}
		if(section.isInt("amount")) {
			amt = section.getInt("amount");
			if(amt < 1)
				amt = 1;
		}
		return new SmeltObjective(amt, mat, dat);
	}
}
