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
	private final boolean inverted;
	
	private MoneyCondition(double amount, boolean invert) {
		this.amount = amount;
		this.inverted = invert;
	}

	@Override
	public boolean isMet(Player player, Quester plugin) {
		if(!Quester.vault) {
			return true;
		}
		return (Quester.econ.getBalance(player.getName()) >= amount) != inverted;
	}
	
	@Override
	protected String parseDescription(String description) {
		return description.replaceAll("%amt", String.valueOf(amount));
	}
	
	@Override
	protected String show() {
		if(Quester.vault) {
			String flag = inverted ? "less than ": "at least ";
			return "Must have " + flag + amount + " " + Quester.econ.currencyNamePlural() + ".";
		}
		else {
			return "Money condition (Met)";
		}
	}
	
	@Override
	protected String info() {
		String flag = inverted ? " (-i)": "";
		return String.valueOf(amount) + flag;
	}
	
	@QCommand(
			min = 1,
			max = 1,
			usage = "<amount> (-i)")
	public static Condition fromCommand(QCommandContext context) throws QCommandException {
		try {
			double amt = context.getDouble(0);
			return new MoneyCondition(amt, context.hasFlag('i'));
		}
		catch (NumberFormatException e) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_GENERAL);
		}
	}

	@Override
	protected void save(StorageKey key) {
		key.setDouble("amount", amount);
		if(inverted) {
			key.setBoolean("inverted", inverted);
		}
	}

	protected static Condition load(StorageKey key) {
		double amt;
		
		try {
			amt = Double.parseDouble(key.getString("amount"));
		}
		catch (Exception e) {
			return null;
		}
		
		return new MoneyCondition(amt, key.getBoolean("inverted", false));
	}
}
