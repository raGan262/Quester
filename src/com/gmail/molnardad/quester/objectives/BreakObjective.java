package com.gmail.molnardad.quester.objectives;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterBreakObjective")
public final class BreakObjective extends Objective {

	private final String TYPE = "BREAK";
	private final Material material;
	private final byte data;
	private final int amount;
	private final int inHand;
	
	public BreakObjective(int amt, Material mat, byte dat, int hnd) {
		amount = amt;
		material = mat;
		data = dat;
		inHand = hnd;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public byte getData() {
		return data;
	}
	
	public boolean checkHand(int itm) {
		return (inHand < 0 || inHand == itm);
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
		String datStr = data < 0 ? " of any type " : " of given type(" + data + ") ";
		String hand = (inHand < 0) ? "" : (inHand == 0) ? "with empty hand " : "with " + Material.getMaterial(inHand).name().toLowerCase().replace('_', ' ') + " ";
		return "Break " + material.name().toLowerCase() + datStr + hand + "- " + (amount - progress) + "x.";
	}
	
	@Override
	public String toString() {
		String dataStr = (data < 0 ? "ANY" : String.valueOf(data));
		return TYPE + ": " + material.name() + "[" + material.getId() + "] DATA: " + dataStr + "; AMT: " + amount + "; HND: " + inHand + stringQevents();
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		
		map.put("material", material.getId());
		map.put("data", data);
		map.put("amount", amount);
		map.put("in-hand", inHand);
		
		return map;
	}

	public static BreakObjective deserialize(Map<String, Object> map) {
		Material mat;
		int dat, amt;
		int hnd = -1;
		
		try {
			mat = Material.getMaterial((Integer) map.get("material"));
			if(mat == null)
				return null;
			dat = (Integer) map.get("data");
			amt = (Integer) map.get("amount");
			if(amt < 1)
				return null;
			if(map.get("in-hand") != null) {
				hnd = (Integer) map.get("in-hand");
			}
			BreakObjective obj = new BreakObjective(amt, mat, (byte)dat, hnd);
			obj.loadSuper(map);
			return obj;
		} catch (Exception e) {
			return null;
		}
	}
}
