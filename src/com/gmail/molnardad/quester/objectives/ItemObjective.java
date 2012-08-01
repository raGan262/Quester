package com.gmail.molnardad.quester.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@SerializableAs("QuesterItemObjective")
public final class ItemObjective implements Objective {

	private final String TYPE = "ITEM";
	private final Material material;
	private final short data;
	private final int amount;
	private final Map<Integer, Integer> enchants;
	
	public ItemObjective(Material mat, int amt, int dat, Map<Integer, Integer> enchs) {
		material = mat;
		amount = amt;
		data = (short)dat;
		enchants = enchs;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public int getTargetAmount() {
		return 0;
	}

	@Override
	public boolean isComplete(Player player, int progress) {
		return false;
	}

	@Override
	public boolean finish(Player player) {
		return takeInventory(player.getInventory());
	}
	
	@Override
	public String progress(int progress) {
		String datStr = data < 0 ? " (any) " : " (data " + data + ") ";
		String pcs = amount == 1 ? " piece of " : " pieces of ";
		String enchs = "\n -- Enchants:";		
		if(enchants.isEmpty()) {
			enchs = "";
		}
		for(Integer i : enchants.keySet()) {
			enchs = enchs + " " + enchantName(i, enchants.get(i)) + ";";
		}
		return "Have " + amount + pcs + material.name().toLowerCase() + datStr + "on completion." + enchs;
	}
	
	@Override
	public String toString() {
		String dataStr = (data < 0 ? "ANY" : String.valueOf(data));
		String itm = material.name()+"["+material.getId()+"]; DMG: "+dataStr+"; AMT: "+amount;
		String enchs = enchants.isEmpty() ? "" : "\n -- ENCH:";
		for(Integer e : enchants.keySet()) {
			enchs = enchs + " " + Enchantment.getById(e).getName() + ":" + enchants.get(e);
		}
		return TYPE+": "+itm+enchs;
	}
	
	public boolean takeInventory(Inventory inv) {
		int remain = amount;
		ItemStack[] contents = inv.getContents();
		for (int i = 0; i <contents.length; i++) {
	        if (contents[i] != null) {
	        	boolean enchsOK = true;
	        	for(Integer e : enchants.keySet()) {
	        		if(enchants.get(e) != contents[i].getEnchantmentLevel(Enchantment.getById(e))) {
	        			enchsOK = false;
	        			break;
	        		}
	        	}
	        	if(enchsOK) {
		        	if (contents[i].getTypeId() == material.getId()) {
		        		if(data < 0 || contents[i].getDurability() == data) {
		        			if(remain >= contents[i].getAmount()) {
		        				remain -= contents[i].getAmount();
		        				contents[i] = null;
		        				inv.clear(i);
		        			} else {
		        				contents[i].setAmount(contents[i].getAmount() - remain);
		        				remain = 0;
		        				break;
		        			}
		        		}
		        	}
	        	}
	        }
	    }
		return remain == 0;
	}
	
	private String enchantName(int id, int lvl) {
		String result = "Unknown";
		switch(id) {
			case 0 : result = "Protection";
					break;
			case 1 : result = "Fire Protection";
					break;
			case 2 : result = "Feather Falling";
					break;
			case 3 : result = "Blast Protection";
					break;
			case 4 : result = "Projectile Protection";
					break;
			case 5 : result = "Respiration";
					break;
			case 6 : result = "Aqua Affinity";
					break;
			case 16 : result = "Sharpness";
					break;
			case 17 : result = "Smite";
					break;
			case 18 : result = "Bane of Arthropods";
					break;
			case 19 : result = "Knockback";
					break;
			case 20 : result = "Fire Aspect";
					break;
			case 21 : result = "Looting";
					break;
			case 32 : result = "Efficiency";
					break;
			case 33 : result = "Silk Touch";
					break;
			case 34 : result = "Unbreaking";
					break;
			case 35 : result = "Fortune";
					break;
			case 48 : result = "Power";
					break;
			case 49 : result = "Punch";
					break;
			case 50 : result = "Flame";
					break;
			case 51 : result = "Infinity";
					break;
		} 
		switch(lvl) {
			case 1 : result = result + " I";
					break;
			case 2 : result = result + " II";
					break;
			case 3 : result = result + " III";
					break;
			case 4 : result = result + " IV";
					break;
			case 5 : result = result + " V";
					break;
		}
		return result;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("material", material.getId());
		map.put("data", data);
		map.put("amount", amount);
		map.put("enchants", enchants);
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static ItemObjective deserialize(Map<String, Object> map) {
		Material mat;
		int dat, amt;
		Map<Integer, Integer> enchs = new HashMap<Integer, Integer>();
		
		try {
			mat = Material.getMaterial((Integer) map.get("material"));
			if(mat == null)
				return null;
			dat = (Integer) map.get("data");
			amt = (Integer) map.get("amount");
			if(amt < 1)
				return null;
			if(map.get("enchants") != null)
				enchs = (Map<Integer, Integer>) map.get("enchants");
			
			return new ItemObjective(mat, amt, dat, enchs);
		} catch (Exception e) {
			return null;
		}
	}
}
