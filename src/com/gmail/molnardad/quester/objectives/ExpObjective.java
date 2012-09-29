package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.ExpManager;

public final class ExpObjective extends Objective {

	public static final String TYPE = "EXP";
	private final int amount;
	
	public ExpObjective(int amt) {
		amount = amt;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean finish(Player player) {
		ExpManager expMan = new ExpManager(player);
		expMan.changeExp(-amount);
		return true;
	}
	
	@Override
	public String progress(int progress) {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%r", String.valueOf(1 - progress)).replaceAll("%t", String.valueOf(amount));
		}
		return "Have " + amount + " experience points on completion.";
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + amount + coloredDesc();
	}
	
	public int takeExp(int amt) {
		return amt - amount;
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
		return new ExpObjective(amt);
	}

	@Override
	public boolean tryToComplete(Player player) {
		int totalExp = new ExpManager(player).getCurrentExp();
		if(totalExp >= amount) {
			finish(player);
			return true;
		}
		return false;
	}
}
