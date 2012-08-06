package com.gmail.molnardad.quester.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterShearObjective")
public final class ShearObjective implements Objective {

	private final String TYPE = "SHEAR";
	private final DyeColor color;
	private final int amount;

	public ShearObjective(int amt, byte dat) {
		amount = amt;
		if(dat < 0)
			color = null;
		else
			color = DyeColor.getByData(dat);
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
		return amount <= progress;
	}

	@Override
	public boolean finish(Player player) {
		return true;
	}

	@Override
	public String progress(int progress) {
		String strCol = (color == null) ? "any" : color.name().replace('_', ' ').toLowerCase() ;
		return "Shear " + strCol + " sheep - " + (amount - progress) + "x";
	}
	
	@Override
	public String toString() {
		String strCol = (color == null) ? "ANY" : color.name() ;
		return TYPE + ": " + strCol + "; AMT: " + amount ;
	}
	
	public boolean check(DyeColor col) {
		if(col == color || color == null) {
			return true;	
		}
		return false;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("amount", amount);
		if(color == null)
			map.put("color", -1);
		else
			map.put("color", color.getData());
		
		return map;
	}

	public static ShearObjective deserialize(Map<String, Object> map) {
		int amt, dat;
		try {
			amt = (Integer) map.get("amount");
			if(amt < 1)
				return null;
			dat = (Integer) map.get("color");
			return new ShearObjective(amt, (byte)dat);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean tryToComplete(Player player) {
		return false;
	}
}
