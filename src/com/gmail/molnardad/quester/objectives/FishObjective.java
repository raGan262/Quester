package com.gmail.molnardad.quester.objectives;

import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterFishObjective")
public final class FishObjective extends Objective {

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
	public String progress(int progress) {
		return "Catch fish - " + (amount - progress) + "x";
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + amount + stringQevents();
	}
	
	public int takeExp(int amt) {
		return amt - amount;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		
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
		
		FishObjective obj = new FishObjective(amt);
		obj.loadQevents(map);
		return obj;
	}
}
