package com.gmail.molnardad.quester.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.utils.Util;

@SerializableAs("QuesterEnchantObjective")
public final class EnchantObjective implements Objective {

	private final String TYPE = "ENCHANT";
	private final Material material;
	private final int amount;
	private final Map<Integer, Integer> enchants;
	
	public EnchantObjective(Material mat, int amt, Map<Integer, Integer> enchs) {
		material = mat;
		amount = amt;
		enchants = enchs;
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
	public boolean finish(Player player) {
		return true;
	}
	
	@Override
	public String progress(int progress) {
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
		return TYPE+": "+itm+enchs;
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
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("material", material.getId());
		map.put("amount", amount);
		map.put("enchants", enchants);
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static EnchantObjective deserialize(Map<String, Object> map) {
		Material mat;
		int amt;
		Map<Integer, Integer> enchs = new HashMap<Integer, Integer>();
		
		try {
			mat = Material.getMaterial((Integer) map.get("material"));
			if(mat == null)
				return null;
			amt = (Integer) map.get("amount");
			if(amt < 1)
				return null;
			if(map.get("enchants") != null)
				enchs = (Map<Integer, Integer>) map.get("enchants");
			
			return new EnchantObjective(mat, amt, enchs);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean tryToComplete(Player player) {
		return false;
	}
}
