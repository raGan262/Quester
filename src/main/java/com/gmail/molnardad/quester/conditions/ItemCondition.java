package com.gmail.molnardad.quester.conditions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.utils.Util;

public final class ItemCondition extends Condition {

	public static final String TYPE = "ITEM";
	private final Material material;
	private final short data;
	private final int amount;
	
	public ItemCondition(Material mat, int amt, int dat) {
		this.material = mat;
		this.amount = amt;
		this.data = (short) dat;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean isMet(Player player) {
        int amt = 0;
        ItemStack[] contents = player.getInventory().getContents();
       
        for (ItemStack i : contents) {
        	if(i == null)
        		continue;
            if (i.getTypeId() == material.getId()) {
            	if(i.getDurability() == data || data < 0) {
                    amt += i.getAmount();
            	}
            	if(amt >= amount)
            		break;
            }
        }
       
        return (amt >= amount);
	}
	
	@Override
	public String show() {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%amt", amount+"").replaceAll("%data", data+"").replaceAll("%id", material.getId()+"");
		}
		String datStr = data < 0 ? " (any) " : " (data " + data + ") ";
		String pcs = amount == 1 ? " piece of " : " pieces of ";
		String mat = material.getId() == 351 ? "dye" : material.name().toLowerCase();
		return "Must have " + amount + pcs + mat + datStr + ".";
	}
	
	@Override
	public String toString() {
		String dataStr = (data < 0 ? "ANY" : String.valueOf(data));
		return TYPE+": "+ material.name()+"["+material.getId()+"]; DMG: "+dataStr+"; AMT: "+amount
				+ coloredDesc().replaceAll("%amt", amount+"").replaceAll("%data", data+"").replaceAll("%id", material.getId()+"");
	}
	
	@Override
	public void serialize(ConfigurationSection section) {
		
		super.serialize(section, TYPE);

		section.set("item", Util.serializeItem(material.getId(), data));
		section.set("amount", amount);
		
	}

	public static ItemCondition deser(ConfigurationSection section) {
		int amt = 1, dat;
		Material mat;
		try {
			int[] itm = Util.parseItem(section.getString("item"));
			mat = Material.getMaterial(itm[0]);
			dat = itm[1];
			if(section.isInt("amount"))
				amt = section.getInt("amount");
			if(amt < 1)
				amt = 1;
		} catch (Exception e) {
			return null;
		}
		return new ItemCondition(mat, amt, dat);
	}
}
