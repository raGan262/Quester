package com.gmail.molnardad.quester.rewards;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

public final class MoneyReward implements Reward {

	private static final long serialVersionUID = 13603L;
	private final String TYPE = "MONEY";
	private final double amount;
	
	public MoneyReward(double amt) {
		this.amount = amt;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean giveReward(Player player) {
		
		EconomyResponse resp;
		
		if(amount >= 0){
			resp = Quester.econ.depositPlayer(player.getName(), amount);
		} else {
			resp = Quester.econ.withdrawPlayer(player.getName(), -amount);
		}
		if(resp.transactionSuccess()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean checkReward(Player player) {
		return Quester.econ.hasAccount(player.getName());
	}
	
	@Override
	public String toString() {
		return TYPE+": "+String.valueOf(amount);
	}
	
	public String checkErrorMessage(){
		return "Player to reward doesn't have economy account.";
	}
	
	public String giveErrorMessage() {
		return "Error occured while trying to pay money reward.";
	}

}
