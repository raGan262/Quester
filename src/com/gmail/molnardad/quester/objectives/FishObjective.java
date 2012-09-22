package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class FishObjective extends Objective {

	public static final String TYPE = "FISH";
	private final int amount;
	
	public FishObjective(int amt) {
		amount = amt;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}

	@Override
	public boolean isComplete(Player player, int progress) {
		return progress >= amount;
	}
	
	@Override
	public String progress(int progress) {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%r", String.valueOf(amount - progress)).replaceAll("%t", String.valueOf(amount));
		}
		return "Catch fish - " + (amount - progress) + "x";
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + amount + coloredDesc() + stringQevents();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		
		section.set("amount", amount);
	}
	
	public static Objective deser(ConfigurationSection section) {
		int amt = 0;
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		if(amt < 1)
			return null;
		return new FishObjective(amt);
	}
}
