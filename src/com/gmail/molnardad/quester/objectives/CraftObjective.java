package com.gmail.molnardad.quester.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SerializableAs("QuesterCraftObjective")
public final class CraftObjective implements Objective {

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
	public boolean finish(Player player) {
		return true;
	}
	
	@Override
	public String progress(int progress) {
		String datStr = data < 0 ? " (any) " : " (data " + data + ") ";
		String pcs = (amount - progress) == 1 ? " piece of " : " pieces of ";
		String mat = material.getId() == 351 ? "dye" : material.name().toLowerCase();
		return "Craft " + (amount - progress) + pcs + mat + datStr + ".";
	}
	
	@Override
	public String toString() {
		String dataStr = (data < 0 ? "ANY" : String.valueOf(data));
		String itm = material.name()+"["+material.getId()+"]; DMG: "+dataStr+"; AMT: "+amount;
		return TYPE+": "+itm;
	}
	
	public boolean check(ItemStack item) {
		if(item.getTypeId() != material.getId())
			return false;
		if(item.getDurability() != data && data >= 0)
			return false;
		return true;
	}

	public boolean checkHand(ItemStack hand, ItemStack item) {
		if(hand.getTypeId() == 0) {
			return true;
		}
		if(item.getTypeId() != hand.getTypeId())
			return false;
		if(item.getDurability() != hand.getDurability())
			return false;
		if((item.getAmount() + hand.getAmount()) > item.getMaxStackSize()) {
			return false;
		}
		return true;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
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
			
			return new CraftObjective(mat, amt, dat);
		} catch (Exception e) {
			return null;
		}
	}
}
