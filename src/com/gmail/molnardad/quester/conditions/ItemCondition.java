package com.gmail.molnardad.quester.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SerializableAs("QuesterItemCondition")
public final class ItemCondition implements Condition {

	private final String TYPE = "ITEM";
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
		String datStr = data < 0 ? " (any) " : " (data " + data + ") ";
		String pcs = amount == 1 ? " piece of " : " pieces of ";
		String mat = material.getId() == 351 ? "dye" : material.name().toLowerCase();
		return "Must have " + amount + pcs + mat + datStr + ".";
	}
	
	@Override
	public String toString() {
		String dataStr = (data < 0 ? "ANY" : String.valueOf(data));
		return TYPE+": "+ material.name()+"["+material.getId()+"]; DMG: "+dataStr+"; AMT: "+amount;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("material", material.getId());
		map.put("data", data);
		map.put("amount", amount);
		
		return map;
	}

	public static ItemCondition deserialize(Map<String, Object> map) {
		int amt, dat;
		Material mat;
		try {
			mat = Material.getMaterial((Integer) map.get("material"));
			if(mat == null)
				return null;
			amt = (Integer) map.get("amount");
			dat = (Integer) map.get("data");
			if(amt < 1)
				return null;
		} catch (Exception e) {
			return null;
		}
		
		return new ItemCondition(mat, amt, dat);
	}
}
