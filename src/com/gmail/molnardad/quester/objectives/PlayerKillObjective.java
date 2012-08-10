package com.gmail.molnardad.quester.objectives;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterPlayerKillObjective")
public final class PlayerKillObjective extends Objective {

	private final String TYPE = "PLAYERKILL";
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
		String player = playerName.equals("") ? "any player" : "player named " + playerName;
		return "Kill " + player + " - " + (amount - progress) + "x";
	}
	
	@Override
	public String toString() {
		String player = playerName.equals("") ? "ANY" : playerName;
		return TYPE + ": " + player + "; AMT: " + amount + stringQevents();
	}
	
	public boolean checkPlayer(Player player) {
		if(playerName.equals("")) {
			return true;
		} else {
			return player.getName().equalsIgnoreCase(playerName);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		
		map.put("amount", amount);
		map.put("wanted", playerName);
		
		return map;
	}

	public static PlayerKillObjective deserialize(Map<String, Object> map) {
		int amt;
		String wanted;
		try {
			amt = (Integer) map.get("amount");
			if(amt < 1)
				return null;
			wanted = (String) map.get("wanted");
			
			PlayerKillObjective obj = new PlayerKillObjective(amt, wanted);
			obj.loadSuper(map);
			return obj;
		} catch (Exception e) {
			return null;
		}
	}
}
