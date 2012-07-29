package com.gmail.molnardad.quester.objectives;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.ExpManager;

public final class ExpObjective implements Objective {

	private static final long serialVersionUID = 13502L;
	private final String TYPE = "EXPERIENCE";
	private final int amount;
	
	public ExpObjective(int amt) {
		amount = amt;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public int getTargetAmount() {
		return 0;
	}

	@Override
	public boolean isComplete(Player player, int progress) {
		return false;
	}

	@Override
	public boolean finish(Player player) {
		ExpManager expMan = new ExpManager(player);
		expMan.changeExp(-amount);
		return true;
	}
	
	@Override
	public String progress(int progress) {
		return "Have " + String.valueOf(amount) + " experience points on completion.";
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + String.valueOf(amount);
	}
	
	public int takeExp(int amt) {
		return amt - amount;
	}

}
