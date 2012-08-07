package com.gmail.molnardad.quester.objectives;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterCollectObjective")
public final class CollectObjective extends Objective {

	private final String TYPE = "COLLECT";
	private final Material material;
	private final short data;
	private final int amount;
	
	public CollectObjective(int amt, Material mat, int dat) {
		amount = amt;
		material = mat;
		data = (short) dat;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public short getData() {
		return data;
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
		String datStr = data < 0 ? " of any type " : " of given type(" + data + ") ";
		return "Collect " + material.name().toLowerCase() + datStr + "- " + (amount - progress) + "x.";
	}
	
	@Override
	public String toString() {
		String dataStr = (data < 0 ? "ANY" : String.valueOf(data));
		return TYPE + ": " + material.name() + "[" + material.getId() + "] DATA: " + dataStr + "; AMT: " + amount + stringQevents();
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		
		map.put("material", material.getId());
		map.put("data", data);
		map.put("amount", amount);
		
		return map;
	}

	public static CollectObjective deserialize(Map<String, Object> map) {
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
			CollectObjective obj = new CollectObjective(amt, mat, (byte)dat);
			obj.loadQevents(map);
			return obj;
		} catch (Exception e) {
			return null;
		}
	}
}
