package com.gmail.molnardad.quester.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterBreakObjective")
public final class BreakObjective implements Objective {

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
		String datStr = data < 0 ? " of any type " : " of given type(" + data + ") ";
		String hand = (inHand < 0) ? "" : (inHand == 0) ? "with empty hand " : "with " + Material.getMaterial(inHand).name().toLowerCase().replace('_', ' ') + " ";
		return "Break " + material.name().toLowerCase() + datStr + hand + "- " + (amount - progress) + "x.";
	}
	
	@Override
	public String toString() {
		String dataStr = (data < 0 ? "ANY" : String.valueOf(data));
		return TYPE + ": " + material.name() + "[" + material.getId() + "] DATA: " + dataStr + "; AMT: " + amount + "; HND: " + inHand;
	}

	@Override
	public boolean finish(Player player) {
		return true;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
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
			
			return new BreakObjective(amt, mat, (byte)dat, hnd);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean tryToComplete(Player player) {
		return false;
	}
}
