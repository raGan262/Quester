package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class BossObjective extends Objective {

	public static final String TYPE = "BOSS";
	private final int amount;
	private final String name;
	
	public BossObjective(String boss, int amt) {
		amount = amt;
		name = boss;
	}
	
	public boolean nameCheck(String boss) {
		return name.equalsIgnoreCase(boss);
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
		return "Kill boss named " + name + " - " + (amount - progress) + "x";
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + name + "; AMT: " + amount + coloredDesc();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);

		section.set("amount", amount);
		section.set("boss", name);
	}
	
	public static Objective deser(ConfigurationSection section) {
		int amt = 0;
		String boss = "";
		boss = section.getString("boss", "");
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		if(amt < 1 || boss.isEmpty())
			return null;
		return new BossObjective(boss, amt);
	}
}
