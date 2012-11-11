package com.gmail.molnardad.quester.conditions;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

public final class MoneyCondition extends Condition {

	public static final String TYPE = "MONEY";
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
		if(!Quester.vault) {
			return true;
		}
		return Quester.econ.getBalance(player.getName()) >= amount;
	}
	
	@Override
	public String show() {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%amt", amount+"");
		}
		if(Quester.vault) {
			return "Must have " + amount + " " + Quester.econ.currencyNamePlural();
		}
		else {
			return "Money condition (Met)";
		}
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + amount + coloredDesc().replaceAll("%amt", amount+"");
	}
	
	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		section.set("amount", amount);
	}

	public static MoneyCondition deser(ConfigurationSection section) {
		int amt;
		
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		else
			return null;
		
		return new MoneyCondition(amt);
	}
}
