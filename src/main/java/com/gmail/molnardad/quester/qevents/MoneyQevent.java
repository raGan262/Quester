package com.gmail.molnardad.quester.qevents;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;

@QElement("MONEY")
public final class MoneyQevent extends Qevent {

	private final double amount;
	
	public MoneyQevent(double amt) {
		this.amount = amt;
	}
	
	@Override
	public String info() {
		return String.valueOf(amount);
	}

	@Override
	protected void run(Player player) {
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

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("amount", amount);
	}
	
	public static MoneyQevent deser(ConfigurationSection section) {
		double amt;
		
		if(section.isInt("amount") || section.isDouble("amount"))
			amt = section.getDouble("amount");
		else
			return null;
		
		return new MoneyQevent(amt);
	}
}
