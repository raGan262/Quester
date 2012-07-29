package com.gmail.molnardad.quester.rewards;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.ExpManager;

public final class ExpReward implements Reward {

	private static final long serialVersionUID = 13601L;
	private final String TYPE = "EXPERIENCE";
	private final int amount;
	
	public ExpReward(int amt) {
		this.amount = amt;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean checkReward(Player player) {
		return true;
	}

	@Override
	public boolean giveReward(Player player) {
		ExpManager expMan = new ExpManager(player);
		expMan.changeExp(amount);
		return true;
	}

	@Override
	public String checkErrorMessage() {
		return "ExpReward checkErrorMessage()";
	}

	@Override
	public String giveErrorMessage() {
		return "ExpReward giveErrorMessage()";
	}
	
	@Override
	public String toString() {
		return TYPE+": "+String.valueOf(amount);
	}

}
