package com.gmail.molnardad.quester.qevents;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Ql;

@QElement("MONEY")
public final class MoneyQevent extends Qevent {
	
	private final double amount;
	
	public MoneyQevent(final double amt) {
		amount = amt;
	}
	
	@Override
	public String info() {
		return String.valueOf(amount);
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		if(!Quester.vault) {
			Ql.warning("Failed process money event on " + player.getName()
					+ ": Economy support disabled");
			return;
		}
		EconomyResponse resp;
		
		if(amount >= 0) {
			resp = Quester.econ.depositPlayer(player.getName(), amount);
		}
		else {
			resp = Quester.econ.withdrawPlayer(player.getName(), -amount);
		}
		if(!resp.transactionSuccess()) {
			Ql.warning("Failed process money event on " + player.getName() + ": "
					+ resp.errorMessage);
		}
	}
	
	@QCommand(min = 1, max = 1, usage = "<amount>")
	public static Qevent fromCommand(final QCommandContext context) throws QCommandException {
		final double amt = context.getDouble(0);
		if(amt == 0.0D) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_NONZERO);
		}
		return new MoneyQevent(context.getDouble(0));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setDouble("amount", amount);
	}
	
	protected static Qevent load(final StorageKey key) {
		final double amt = key.getDouble("amount", 0.0D);
		if(amt == 0) {
			return null;
		}
		
		return new MoneyQevent(amt);
	}
}
