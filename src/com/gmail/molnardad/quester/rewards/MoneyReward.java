package com.gmail.molnardad.quester.rewards;

import java.util.HashMap;
import java.util.Map;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

@SerializableAs("QuesterMoneyReward")
public final class MoneyReward implements Reward {

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

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("amount", amount);
		
		return map;
	}

	public static MoneyReward deserialize(Map<String, Object> map) {
		double amt;
		
		try {
			amt = (Double) map.get("amount");
			return new MoneyReward(amt);
		} catch (Exception e) {
			return null;
		}
	}

}
