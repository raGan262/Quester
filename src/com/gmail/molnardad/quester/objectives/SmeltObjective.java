package com.gmail.molnardad.quester.objectives;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SerializableAs("QuesterSmeltObjective")
public final class SmeltObjective extends Objective {

	private final String TYPE = "SMELT";
	private final Material material;
	private final short data;
	private final int amount;
	
	public SmeltObjective(Material mat, int amt, int dat) {
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
		String datStr = data < 0 ? " (any) " : " (data " + data + ") ";
		String pcs = (amount - progress) == 1 ? " piece of " : " pieces of ";
		String mat = material.getId() == 351 ? "dye" : material.name().toLowerCase();
		return "Smelt " + (amount - progress) + pcs + mat + datStr + ".";
	}
	
	@Override
	public String toString() {
		String dataStr = (data < 0 ? "ANY" : String.valueOf(data));
		String itm = material.name()+"["+material.getId()+"]; DMG: "+dataStr+"; AMT: "+amount + stringQevents();
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
	
	public static SmeltObjective deserialize(Map<String, Object> map) {
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
			
			SmeltObjective obj = new SmeltObjective(mat, amt, dat);
			obj.loadQevents(map);
			return obj;
		} catch (Exception e) {
			return null;
		}
	}
}
