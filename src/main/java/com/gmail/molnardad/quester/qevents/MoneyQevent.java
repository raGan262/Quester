package com.gmail.molnardad.quester.qevents;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("MONEY")
public final class MoneyQevent extends Qevent {

	/**
	 * @uml.property  name="amount"
	 */
	private final double amount;
	
	public MoneyQevent(double amt) {
		this.amount = amt;
	}
	
	@Override
	public String info() {
		return String.valueOf(amount);
	}

	@Override
	protected void run(Player player, Quester plugin) {
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

	@QCommand(
			min = 1,
			max = 1,
			usage = "<amount>")
	public static Qevent fromCommand(QCommandContext context) {
		return new MoneyQevent(context.getDouble(0));
	}

	@Override
	protected void save(StorageKey key) {
		key.setDouble("amount", amount);
	}
	
	protected static Qevent load(StorageKey key) {
		double amt;
		
		amt = key.getDouble("amount", 0.0D);
		if(amt == 0) {
			return null;
		}
		
		return new MoneyQevent(amt);
	}
}
