package com.gmail.molnardad.quester.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.ExpManager;

@SerializableAs("QuesterExpReward")
public final class ExpReward implements Reward {

	private final String TYPE = "EXPERIENCE";
	private final int amount;
	
	public ExpReward(int amt) {
		this.amount = amt;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean checkReward(Player player) {
		return true;
	}

	@Override
	public boolean giveReward(Player player) {
		ExpManager expMan = new ExpManager(player);
		expMan.changeExp(amount);
		return true;
	}

	@Override
	public String checkErrorMessage() {
		return "ExpReward checkErrorMessage()";
	}

	@Override
	public String giveErrorMessage() {
		return "ExpReward giveErrorMessage()";
	}
	
	@Override
	public String toString() {
		return TYPE+": "+String.valueOf(amount);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("amount", amount);
		
		return map;
	}

	public static ExpReward deserialize(Map<String, Object> map) {
		int amt;
		
		try {
			amt = (Integer) map.get("amount");
		} catch (Exception e) {
			return null;
		}
		
		return new ExpReward(amt);
	}
}
