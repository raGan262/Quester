package me.ragan262.quester.objectives;

import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;

import org.bukkit.entity.Player;

@QElement("PLAYERKILL")
public final class PlayerKillObjective extends Objective {
	
	private final String playerName;
	private final int amount;
	private final boolean perm;
	
	public PlayerKillObjective(final int amt, final String name, final boolean perm) {
		amount = amt;
		playerName = name;
		this.perm = perm;
	}
	
	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(final int progress) {
		String player = playerName.isEmpty() ? "any player" : "player named " + playerName;
		if(perm) {
			player = "player with permission " + playerName;
		}
		return "Kill " + player + " - " + (amount - progress) + "x";
	}
	
	@Override
	protected String info() {
		final String player = playerName.isEmpty() ? "ANY" : playerName;
		return player + "; AMT: " + amount + "; PERM: " + perm;
	}
	
	@QCommand(min = 1, max = 2, usage = "<amount> [player] (-p)")
	public static Objective fromCommand(final QCommandContext context) {
		final int amt = context.getInt(0);
		final boolean perm = context.hasFlag('p');
		String name = "";
		if(context.length() > 1) {
			name = context.getString(1);
		}
		return new PlayerKillObjective(amt, name, perm);
	}
	
	@Override
	protected void save(final StorageKey key) {
		if(amount > 1) {
			key.setInt("amount", amount);
		}
		if(!playerName.isEmpty()) {
			key.setString("name", playerName);
		}
		if(perm) {
			key.setBoolean("perm", true);
		}
	}
	
	protected static Objective load(final StorageKey key) {
		int amt = 1;
		String name = "";
		boolean prm = false;
		name = key.getString("name", "");
		amt = key.getInt("amount");
		if(amt < 1) {
			amt = 1;
		}
		prm = key.getBoolean("perm", false);
		return new PlayerKillObjective(amt, name, prm);
	}
	
	// Custom methods
	
	public boolean checkPlayer(final Player player) {
		if(playerName.isEmpty()) {
			return true;
		}
		else if(perm) {
			return player.hasPermission(playerName);
		}
		else {
			return player.getName().equalsIgnoreCase(playerName);
		}
	}
}
