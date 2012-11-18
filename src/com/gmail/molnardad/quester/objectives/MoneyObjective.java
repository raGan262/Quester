package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

public final class MoneyObjective extends Objective {

	public static final String TYPE = "MONEY";
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
		if(Quester.vault) {
			Quester.econ.withdrawPlayer(player.getName(), amount);
		}
		return true;
	}
	
	@Override
	public String progress(int progress) {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%r", String.valueOf(1 - progress)).replaceAll("%t", String.valueOf(amount));
		}
		if(Quester.vault) {
			return "Get " + amount + " " + Quester.econ.currencyNamePlural();
		}
		else {
			return "Economy support disabled. (Completed)";
		}
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + amount + coloredDesc();
	}
	
	public double takeMoney(double amt) {
		return amt - amount;
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		
		section.set("amount", amount);
	}
	
	public static Objective deser(ConfigurationSection section) {
		double amt = 0;
		if(section.isInt("amount") || section.isDouble("amount"))
			amt = section.getDouble("amount");
		if(amt <= 0)
			return null;
		return new MoneyObjective(amt);
	}

	@Override
	public boolean tryToComplete(Player player) {
		if(!Quester.vault) {
			return true;
		}
		double money = Quester.econ.getBalance(player.getName());
		if(money >= amount) {
			finish(player);
			return true;
		}
		return false;
	}
}
