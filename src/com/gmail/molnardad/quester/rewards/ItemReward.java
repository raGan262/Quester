package com.gmail.molnardad.quester.rewards;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ItemReward implements Reward {

	private static final long serialVersionUID = 13602L;
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
}
