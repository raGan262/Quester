package com.gmail.molnardad.quester.objectives;

import org.bukkit.entity.Player;

public final class PlayerKillObjective implements Objective {

	private static final long serialVersionUID = 13507L;
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

}
