package com.gmail.molnardad.quester.objectives;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;

@QElement("PLAYERKILL")
public final class PlayerKillObjective extends Objective {

	private final String playerName;
	private final int amount;
	private final boolean perm;
	
	public PlayerKillObjective(int amt, String name, boolean perm) {
		amount = amt;
		playerName = name;
		this.perm = perm;
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}

	@Override
	protected String show(int progress) {
		String player = playerName.isEmpty() ? "any player" : "player named " + playerName;
		if(perm) {
			player = "player with permission " + playerName;
		}
		return "Kill " + player + " - " + (amount - progress) + "x";
	}
	
	@Override
	protected String info() {
		String player = playerName.isEmpty() ? "ANY" : playerName;
		return player + "; AMT: " + amount + "; PERM: " + perm;
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		if(amount > 1)
			section.set("amount", amount);
		if(!playerName.isEmpty())
			section.set("name", playerName);
		if(perm)
			section.set("perm", true);
	}
	
	public static Objective deser(ConfigurationSection section) {
		int amt = 1;
		String name = "";
		boolean prm = false;
		name = section.getString("name", "");
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		if(amt < 1)
			amt = 1;
		prm = section.getBoolean("perm", false);
		return new PlayerKillObjective(amt, name, prm);
	}
	
	//Custom methods
	
	public boolean checkPlayer(Player player) {
		if(perm) {
			return player.hasPermission(playerName);
		}
		else if(playerName.isEmpty()) {
			return true;
		} 
		else {
			return player.getName().equalsIgnoreCase(playerName);
		}
	}
}
