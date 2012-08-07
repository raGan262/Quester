package com.gmail.molnardad.quester.objectives;

import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.ExpManager;

@SerializableAs("QuesterExpObjective")
public final class ExpObjective extends Objective {

	private final String TYPE = "EXPERIENCE";
	private final int amount;
	
	public ExpObjective(int amt) {
		amount = amt;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean finish(Player player) {
		ExpManager expMan = new ExpManager(player);
		expMan.changeExp(-amount);
		return true;
	}
	
	@Override
	public String progress(int progress) {
		return "Have " + String.valueOf(amount) + " experience points on completion.";
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + String.valueOf(amount) + stringQevents();
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

	public static ExpObjective deserialize(Map<String, Object> map) {
		int amt;
		
		try {
			amt = (Integer) map.get("amount");
			if(amt < 1)
				return null;
		} catch (Exception e) {
			return null;
		}
		
		ExpObjective obj = new ExpObjective(amt);
		obj.loadQevents(map);
		return obj;
	}

	@Override
	public boolean tryToComplete(Player player) {
		int totalExp = new ExpManager(player).getCurrentExp();
		if(totalExp >= amount) {
			finish(player);
			return true;
		}
		return false;
	}
}
