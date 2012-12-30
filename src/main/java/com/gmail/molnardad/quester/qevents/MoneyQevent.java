package com.gmail.molnardad.quester.qevents;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

public final class MoneyQevent extends Qevent {

	public static final String TYPE = "MONEY";
	private final double amount;
	
	public MoneyQevent(int occ, int del, double amt) {
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
		return TYPE + ": " + amount + appendSuper();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		section.set("amount", amount);
	}
	
	public static MoneyQevent deser(int occ, int del, ConfigurationSection section) {
		double amt;
		
		if(section.isInt("amount") || section.isDouble("amount"))
			amt = section.getDouble("amount");
		else
			return null;
		
		return new MoneyQevent(occ, del, amt);
	}

	@Override
	public void run(Player player) {
		if(!Quester.vault) {
			Quester.log.info("Failed process money event on " + player.getName() + ": Economy support disabled");
			return;
		}
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
