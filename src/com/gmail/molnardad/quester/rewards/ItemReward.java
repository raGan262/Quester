package com.gmail.molnardad.quester.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@SerializableAs("QuesterItemReward")
public final class ItemReward implements Reward {

	private final String TYPE = "ITEM";
	private final Material material;
	private final int amount;
	private final short data;
	private final Map<Integer, Integer> enchants;

	public ItemReward(Material mat, int amt, int dat, Map<Integer, Integer> enchs) {
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
	public boolean giveReward(Player player) {
		ItemStack item = new ItemStack(material, amount, data);
		for(Integer i : enchants.keySet()) {
			item.addEnchantment(Enchantment.getById(i), enchants.get(i));
		}
		player.getInventory().addItem(item);
		return true;
	}
	
	@Override
	public boolean checkReward(Player player) {
        return checkInventory(player.getInventory());
	}

	@Override
	public String checkErrorMessage() {
		return "Not enough space for item reward.";
	}

	@Override
	public String giveErrorMessage() {
		return "ItemReward giveErrorMessage() (should not occur)";
	}
	
	@Override
	public String toString() {
		String itm = material.name()+"["+material.getId()+"]; DMG: "+data+"; AMT: "+amount;
		String enchs = enchants.isEmpty() ? "" : "\n -- ENCH:";
		for(Integer e : enchants.keySet()) {
			enchs = enchs + " " + Enchantment.getById(e).getName() + ":" + enchants.get(e);
		}
		return TYPE+": "+itm+enchs;
	}
	
	public boolean giveInventory(Inventory inv) {
		ItemStack item = new ItemStack(material, amount, data);
		for(Integer i : enchants.keySet()) {
			item.addEnchantment(Enchantment.getById(i), enchants.get(i));
		}
		inv.addItem(item);
		return true;
	}
	
	public boolean checkInventory(Inventory inv) {
        int maxSize = material.getMaxStackSize();
        int numSpaces = 0;
        ItemStack[] contents = inv.getContents();
       
        for (ItemStack i : contents) {
            if (i == null) {
                numSpaces += maxSize;
            } else if (i.getType().equals(material) && enchants.isEmpty()) {
            	if(i.getDurability() == data) {
                    numSpaces += (maxSize - i.getAmount());
            	}
            }
        }
       
        return (numSpaces >= amount);
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
	public static ItemReward deserialize(Map<String, Object> map) {
		Material mat;
		int dat, amt;
		Map<Integer, Integer> enchs = new HashMap<Integer, Integer>();
		
		try {
			mat = Material.getMaterial((Integer) map.get("material"));
			if(mat == null)
				return null;
			dat = (Integer) map.get("data");
			if(dat < 0)
				return null;
			amt = (Integer) map.get("amount");
			if(amt < 1)
				return null;
			if(map.get("enchants") != null)
				enchs = (Map<Integer, Integer>) map.get("enchants");
			
			return new ItemReward(mat, amt, dat, enchs);
		} catch (Exception e) {
			return null;
		}
	}
}
