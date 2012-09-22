package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class PlayerKillObjective extends Objective {

	public static final String TYPE = "PLAYERKILL";
	private final String playerName;
	private final int amount;
	
	public PlayerKillObjective(int amt, String name) {
		amount = amt;
		playerName = name;
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
		String player = playerName.isEmpty() ? "any player" : "player named " + playerName;
		return "Kill " + player + " - " + (amount - progress) + "x";
	}
	
	@Override
	public String toString() {
		String player = playerName.isEmpty() ? "ANY" : playerName;
		return TYPE + ": " + player + "; AMT: " + amount + coloredDesc() + stringQevents();
	}
	
	public boolean checkPlayer(Player player) {
		if(playerName.isEmpty()) {
			return true;
		} else {
			return player.getName().equalsIgnoreCase(playerName);
		}
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		
		if(amount > 1)
			section.set("amount", amount);
		if(!playerName.isEmpty())
			section.set("name", playerName);
	}
	
	public static Objective deser(ConfigurationSection section) {
		int amt = 1;
		String name;
		name = section.getString("name");
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		if(amt < 1)
			amt = 1;
		return new PlayerKillObjective(amt, name);
	}
}
