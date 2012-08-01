package com.gmail.molnardad.quester.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterPlayerKillObjective")
public final class PlayerKillObjective implements Objective {

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
	public boolean finish(Player player) {
		return true;
	}

	@Override
	public String progress(int progress) {
		String player = playerName.equals("") ? "any player" : "player named " + playerName;
		return "Kill " + player + " - " + (amount - progress) + "x";
	}
	
	@Override
	public String toString() {
		String player = playerName.equals("") ? "ANY" : playerName;
		return TYPE + ": " + player + "; AMT: " + amount;
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
		Map<String, Object> map = new HashMap<String, Object>();
		
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
			
			return new PlayerKillObjective(amt, wanted);
		} catch (Exception e) {
			return null;
		}
	}
}
