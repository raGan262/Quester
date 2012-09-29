package com.gmail.molnardad.quester.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.utils.Util;

public final class EnchantObjective extends Objective {

	public static final String TYPE = "ENCHANT";
	private final Material material;
	private final int amount;
	private final Map<Integer, Integer> enchants;
	
	public EnchantObjective(Material mat, int amt, Map<Integer, Integer> enchs) {
		material = mat;
		amount = amt;
		if(enchs != null)
			this.enchants = enchs;
		else
			this.enchants = new HashMap<Integer, Integer>();
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
		String pcs = amount == 1 ? " piece of " : " pieces of ";
		String enchs = "\n -- Required enchants:";
		if(enchants.isEmpty()) {
			enchs = "";
		}
		for(Integer i : enchants.keySet()) {
			enchs = enchs + " " + Util.enchantName(i, enchants.get(i)) + ";";
		}
		return "Enchant " + (amount - progress) + pcs + material.name() + "." + enchs;
	}
	
	@Override
	public String toString() {
		String itm = material.name()+"["+material.getId()+"]; AMT: "+amount;
		String enchs = enchants.isEmpty() ? "" : "\n -- ENCH:";
		for(Integer e : enchants.keySet()) {
			enchs = enchs + " " + Enchantment.getById(e).getName() + ":" + enchants.get(e);
		}
		return TYPE+": "+itm+enchs + coloredDesc();
	}
	
	public boolean check(ItemStack item, Map<Enchantment, Integer> enchs) {
		if(item.getTypeId() != material.getId())
			return false;
		for(int i : enchants.keySet()) {
			if(enchs.get(Enchantment.getById(i)) == null) {
				return false;
			} else if(enchs.get(Enchantment.getById(i)) < enchants.get(i)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);


		section.set("enchants", Util.serializeEnchants(enchants));
		if(material != null)
			section.set("item", Util.serializeItem(material, -1));
		if(amount != 1)
			section.set("amount", amount);
	}
	
	public static Objective deser(ConfigurationSection section) {
		Material mat = null;
		int amt = 1;
		Map<Integer, Integer> enchs = null;
		try {
			if(section.isString("item")) {
				try {
				mat = Material.getMaterial(Util.parseItem(section.getString("item"))[0]);
				} catch (Exception ignore) {}
			}
			
			if(section.isInt("amount"))
				amt = section.getInt("amount");
			if(amt < 1)
				amt = 1;
			
			if(section.isString("enchants"))
				enchs = Util.parseEnchants(section.getString("enchants"));
			else
				return null;
		} catch (Exception e) {
			return null;
		}
		
		return new EnchantObjective(mat, amt, enchs);
	}
}
