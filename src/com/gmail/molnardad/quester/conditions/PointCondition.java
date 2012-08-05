package com.gmail.molnardad.quester.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

@SerializableAs("QuesterPointCondition")
public final class PointCondition implements Condition {

	private final String TYPE = "POINT";
	private final int amount;
	
	public PointCondition(int amount) {
		this.amount = amount;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean isMet(Player player) {
		return Quester.qMan.getProfile(player.getName()).getPoints() >= amount;
	}
	
	@Override
	public String show() {
		return "Must have " + amount + " quest points.";
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + amount;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("amount", amount);
		
		return map;
	}

	public static PointCondition deserialize(Map<String, Object> map) {
		int qst;
		try {
			qst = (Integer) map.get("amount");
		} catch (Exception e) {
			return null;
		}
		
		return new PointCondition(qst);
	}
}
