package com.gmail.molnardad.quester.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.utils.Util;

public final class ItemObjective extends Objective {

	public static final String TYPE = "ITEM";
	private final Material material;
	private final short data;
	private final int amount;
	private final Map<Integer, Integer> enchants;
	
	public ItemObjective(Material mat, int amt, int dat, Map<Integer, Integer> enchs) {
		material = mat;
		amount = amt;
		data = (short)dat;
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
		String dataStr = (data < 0 ? "" : ":" + data);
		String itm = material.name() + "["+material.getId() + dataStr + "]; AMT: " + amount;
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
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);

		section.set("item", Util.serializeItem(material, data));
		if(!enchants.isEmpty())
			section.set("enchants", Util.serializeEnchants(enchants));
		if(amount != 1)
			section.set("amount", amount);
	}
	
	public static Objective deser(ConfigurationSection section) {
		Material mat = null;
		int dat = 0, amt = 1;
		Map<Integer, Integer> enchs = null;
		try {
			int[] itm = Util.parseItem(section.getString("item", ""));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
			
			if(section.isInt("amount"))
				amt = section.getInt("amount");
			if(amt < 1)
				amt = 1;
			
			if(section.isString("enchants"))
				try {
					enchs = Util.parseEnchants(section.getString("enchants"));
				} catch (QuesterException e) {
					enchs = null;
				}
		} catch (Exception e) {
			return null;
		}
		
		return new ItemObjective(mat, dat, amt, enchs);
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
