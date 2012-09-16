package com.gmail.molnardad.quester.qevents;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

public final class MoneyQevent extends Qevent {

	public static final String TYPE = "MONEY";
	private final int amount;
	
	public MoneyQevent(int occ, int del, int amt) {
		super(occ, del);
		this.amount = amt;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public int getOccasion() {
		return occasion;
	}
	
	@Override
	public String toString() {
		return TYPE + ": AMT: " + amount;
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		section.set("amount", amount);
	}
	
	public static MoneyQevent deser(int occ, int del, ConfigurationSection section) {
		int amt;
		
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		else
			return null;
		
		return new MoneyQevent(occ, del, amt);
	}

	@Override
	public void run(Player player) {
		EconomyResponse resp;
		
		if(amount >= 0){
			resp = Quester.econ.depositPlayer(player.getName(), amount);
		} else {
			resp = Quester.econ.withdrawPlayer(player.getName(), -amount);
		}
		if(!resp.transactionSuccess()) {
			Quester.log.info("Failed process money event on " + player.getName() + ": " + resp.errorMessage);
		}
	}
}
