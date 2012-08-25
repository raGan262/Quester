package com.gmail.molnardad.quester.objectives;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

@SerializableAs("QuesterMoneyObjective")
public final class MoneyObjective extends Objective {

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
	public boolean finish(Player player) {
		Quester.econ.withdrawPlayer(player.getName(), amount);
		return true;
	}
	
	@Override
	public String progress(int progress) {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%r", String.valueOf(1 - progress)).replaceAll("%t", String.valueOf(amount));
		}
		return "Get " + amount + " " + Quester.econ.currencyNamePlural();
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + amount + coloredDesc() + stringQevents();
	}
	
	public double takeMoney(double amt) {
		return amt - amount;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		
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
		
		MoneyObjective obj = new MoneyObjective(amt);
		obj.loadSuper(map);
		return obj;
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
