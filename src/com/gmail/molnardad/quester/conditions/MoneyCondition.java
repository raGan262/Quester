package com.gmail.molnardad.quester.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

@SerializableAs("QuesterMoneyCondition")
public final class MoneyCondition implements Condition {

	private final String TYPE = "MONEY";
	private final int amount;
	
	public MoneyCondition(int amount) {
		this.amount = amount;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean isMet(Player player) {
		return Quester.econ.getBalance(player.getName()) >= amount;
	}
	
	@Override
	public String show() {
		return "Must have " + amount + " " + Quester.econ.currencyNamePlural();
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

	public static MoneyCondition deserialize(Map<String, Object> map) {
		int qst;
		try {
			qst = (Integer) map.get("amount");
		} catch (Exception e) {
			return null;
		}
		
		return new MoneyCondition(qst);
	}
}
