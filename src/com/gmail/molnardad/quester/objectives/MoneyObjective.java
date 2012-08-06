package com.gmail.molnardad.quester.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

@SerializableAs("QuesterMoneyObjective")
public final class MoneyObjective implements Objective {

	private final String TYPE = "MONEY";
	private final double amount;
	
	public MoneyObjective(double amt) {
		amount = amt;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public int getTargetAmount() {
		return 1;
	}

	@Override
	public boolean isComplete(Player player, int progress) {
		return progress >= 1;
	}

	
	@Override
	public boolean finish(Player player) {
		Quester.econ.withdrawPlayer(player.getName(), amount);
		return true;
	}
	
	@Override
	public String progress(int progress) {
		return "Get " + amount + " " + Quester.econ.currencyNamePlural();
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + amount;
	}
	
	public double takeMoney(double amt) {
		return amt - amount;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("amount", amount);
		
		return map;
	}

	public static MoneyObjective deserialize(Map<String, Object> map) {
		double amt;
		
		try {
			amt = (Double) map.get("amount");
			if(amt <= 0)
				return null;
		} catch (Exception e) {
			return null;
		}
		
		return new MoneyObjective(amt);
	}

	@Override
	public boolean tryToComplete(Player player) {
		double money = Quester.econ.getBalance(player.getName());
		if(money >= amount) {
			finish(player);
			return true;
		}
		return false;
	}
}
