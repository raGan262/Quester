package com.gmail.molnardad.quester.objectives;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SerializableAs("QuesterCraftObjective")
public final class CraftObjective extends Objective {

	private final String TYPE = "CRAFT";
	private final Material material;
	private final short data;
	private final int amount;
	
	public CraftObjective(Material mat, int amt, int dat) {
		material = mat;
		amount = amt;
		data = (short)dat;
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
		String datStr = data < 0 ? " (any) " : " (data " + data + ") ";
		String pcs = (amount - progress) == 1 ? " piece of " : " pieces of ";
		String mat = material.getId() == 351 ? "dye" : material.name().toLowerCase();
		return "Craft " + (amount - progress) + pcs + mat + datStr + ".";
	}
	
	@Override
	public String toString() {
		String dataStr = (data < 0 ? "ANY" : String.valueOf(data));
		String itm = material.name()+"["+material.getId()+"]; DMG: "+dataStr+"; AMT: "+amount + coloredDesc() + stringQevents();
		return TYPE+": "+itm;
	}
	
	public boolean check(ItemStack item) {
		if(item.getTypeId() != material.getId())
			return false;
		if(item.getDurability() != data && data >= 0)
			return false;
		return true;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		
		map.put("material", material.getId());
		map.put("data", data);
		map.put("amount", amount);
		
		return map;
	}
	
	public static CraftObjective deserialize(Map<String, Object> map) {
		Material mat;
		int dat, amt;
		
		try {
			mat = Material.getMaterial((Integer) map.get("material"));
			if(mat == null)
				return null;
			dat = (Integer) map.get("data");
			amt = (Integer) map.get("amount");
			if(amt < 1)
				return null;
			CraftObjective obj =new CraftObjective(mat, amt, dat) ;
			obj.loadSuper(map);
			return obj;
		} catch (Exception e) {
			return null;
		}
	}
}
