package com.gmail.molnardad.quester.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterFishObjective")
public final class FishObjective implements Objective {

	private final String TYPE = "FISH";
	private final int amount;
	
	public FishObjective(int amt) {
		amount = amt;
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
		return "Catch fish - " + (amount - progress) + "x";
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + amount;
	}
	
	public int takeExp(int amt) {
		return amt - amount;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("amount", amount);
		
		return map;
	}

	public static FishObjective deserialize(Map<String, Object> map) {
		int amt;
		
		try {
			amt = (Integer) map.get("amount");
			if(amt < 1)
				return null;
		} catch (Exception e) {
			return null;
		}
		
		return new FishObjective(amt);
	}

	@Override
	public boolean tryToComplete(Player player) {
		return false;
	}
}
