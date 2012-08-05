package com.gmail.molnardad.quester.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

@SerializableAs("QuesterPointReward")
public final class PointReward implements Reward {

	private final String TYPE = "POINT";
	private final int amount;
	
	public PointReward(int amt) {
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
		Quester.qMan.getProfile(player.getName()).addPoints(amount);
		return true;
	}

	@Override
	public String checkErrorMessage() {
		return "PointReward checkErrorMessage()";
	}

	@Override
	public String giveErrorMessage() {
		return "PointReward giveErrorMessage()";
	}
	
	@Override
	public String toString() {
		return TYPE+": " + amount;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("amount", amount);
		
		return map;
	}

	public static PointReward deserialize(Map<String, Object> map) {
		int amt;
		
		try {
			amt = (Integer) map.get("amount");
		} catch (Exception e) {
			return null;
		}
		
		return new PointReward(amt);
	}
}
