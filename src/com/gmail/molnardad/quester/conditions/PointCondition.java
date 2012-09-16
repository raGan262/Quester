package com.gmail.molnardad.quester.conditions;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

public final class PointCondition extends Condition {

	public static final String TYPE = "POINT";
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
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%amt", amount+"");
		}
		return "Must have " + amount + " quest points.";
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
