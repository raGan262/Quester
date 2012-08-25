package com.gmail.molnardad.quester.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.utils.Util;

@SerializableAs("QuesterItemObjective")
public final class ItemObjective extends Objective {

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
	public boolean finish(Player player) {
		return takeInventory(player.getInventory());
	}
	
	@Override
	public String progress(int progress) {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%r", String.valueOf(1 - progress)).replaceAll("%t", String.valueOf(amount));
		}
		String datStr = data < 0 ? " (any) " : " (data " + data + ") ";
		String pcs = amount == 1 ? " piece of " : " pieces of ";
		String enchs = "\n -- Enchants:";
		String mat = material.getId() == 351 ? "dye" : material.name().toLowerCase();
		if(enchants.isEmpty()) {
			enchs = "";
		}
		for(Integer i : enchants.keySet()) {
			enchs = enchs + " " + Util.enchantName(i, enchants.get(i)) + ";";
		}
		return "Have " + amount + pcs + mat + datStr + "on completion." + enchs;
	}
	
	@Override
	public String toString() {
		String dataStr = (data < 0 ? "ANY" : String.valueOf(data));
		String itm = material.name()+"["+material.getId()+"]; DMG: "+dataStr+"; AMT: "+amount;
		String enchs = enchants.isEmpty() ? "" : "\n -- ENCH:";
		for(Integer e : enchants.keySet()) {
			enchs = enchs + " " + Enchantment.getById(e).getName() + ":" + enchants.get(e);
		}
		return TYPE+": "+itm+enchs + coloredDesc() + stringQevents();
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

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		
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
			
			ItemObjective obj = new ItemObjective(mat, amt, dat, enchs);
			obj.loadSuper(map);
			return obj;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean tryToComplete(Player player) {
		Inventory newInv = QuestManager.createInventory(player);
		if(takeInventory(newInv)) {
			takeInventory(player.getInventory());
			return true;
		}
		return false;
	}
}
