package com.gmail.molnardad.quester.conditions;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("MONEY")
public final class MoneyCondition extends Condition {

	private final double amount;
	
	public MoneyCondition(double amount) {
		this.amount = amount;
	}

	@Override
	public boolean isMet(Player player, Quester plugin) {
		if(!Quester.vault) {
			return true;
		}
		return Quester.econ.getBalance(player.getName()) >= amount;
	}
	
	@Override
	protected String parseDescription(String description) {
		return description.replaceAll("%amt", String.valueOf(amount));
	}
	
	@Override
	protected String show() {
		if(Quester.vault) {
			return "Must have " + amount + " " + Quester.econ.currencyNamePlural();
		}
		else {
			return "Money condition (Met)";
		}
	}
	
	@Override
	protected String info() {
		return String.valueOf(amount);
	}
	
	@QCommand(
			min = 1,
			max = 1,
			usage = "<amount>")
	public static Condition fromCommand(QCommandContext context) throws QCommandException {
		try {
			double amt = context.getDouble(1);
			return new MoneyCondition(amt);
		}
		catch (NumberFormatException e) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_GENERAL);
		}
	}
	
	protected void save(StorageKey key) {
		key.setDouble("amount", amount);
	}

	protected static Condition load(StorageKey key) {
		double amt;
		
		try {
			amt = Double.parseDouble(key.getString("amount"));
		}
		catch (Exception e) {
			return null;
		}
		
		return new MoneyCondition(amt);
	}
}
