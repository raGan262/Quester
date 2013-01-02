package com.gmail.molnardad.quester.qevents;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.utils.Util;

public final class ItemQevent extends Qevent {

	public static final String TYPE = "ITEM";
	public final Material material;
	public final short data;
	private final int amount;
	private final Map<Integer, Integer> enchants;
	
	public ItemQevent(int occ, int del, Material mat, int dat, int amt, Map<Integer, Integer> enchs) {
		super(occ, del);
		this.material = mat;
		this.data = (short) dat;
		this.amount = amt;
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
	public int getOccasion() {
		return occasion;
	}
	
	@Override
	public String toString() {
		String itm = material.name()+"["+material.getId()+":"+data+"]; AMT: "+amount;
		String enchs = enchants.isEmpty() ? "" : "\n -- ENCH:";
		for(Integer e : enchants.keySet()) {
			enchs = enchs + " " + Enchantment.getById(e).getName() + ":" + enchants.get(e);
		}
		return TYPE+": "+itm + appendSuper() + enchs;
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
	
	public static ItemQevent deser(int occ, int del, ConfigurationSection section) {
		Material mat = null;
		int dat = 0, amt = 1;
		Map<Integer, Integer> enchs = null;
		try {
			int[] itm = Util.parseItem(section.getString("item"));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
			if(dat < 0)
				dat = 0;
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
		
		return new ItemQevent(occ, del, mat, dat, amt, enchs);
	}

	@Override
	void run(Player player) {
		int maxSize = material.getMaxStackSize();
        int toGive = amount;
        int numSpaces = 0;
        int given = 0;
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack i : contents) {
            if (i == null) {
                numSpaces += maxSize;
            } 
            else if (i.getType().equals(material) && enchants.isEmpty() && i.getDurability() == data) {
                   numSpaces += (maxSize - i.getAmount());
            }
        }
        given = Math.min(toGive, numSpaces);
        toGive -= given;
        numSpaces = (int) Math.ceil((double)given / (double)maxSize);
        int round;
        ItemStack item;
        PlayerInventory inv = player.getInventory();
        for(int k=0; k<numSpaces; k++) {
        	round = Math.min(maxSize, given);
	        item = new ItemStack(material, round, data);
	        for(Integer j : enchants.keySet()) {
				item.addEnchantment(Enchantment.getById(j), enchants.get(j));
			}
	        inv.addItem(item);
	        given -= round;
        }

        if(toGive > 0) {
            numSpaces = (int) Math.ceil((double)toGive / (double)maxSize);
        	for(int k=0; k<numSpaces; k++) {
        		given = Math.min(toGive, maxSize);
	        	item = new ItemStack(material, given, data);
	        	for(Integer j : enchants.keySet()) {
        			item.addEnchantment(Enchantment.getById(j), enchants.get(j));
        		}
	        	player.getWorld().dropItem(player.getLocation(), item);
	        	toGive -= given;
        	}
        }
	}
}
