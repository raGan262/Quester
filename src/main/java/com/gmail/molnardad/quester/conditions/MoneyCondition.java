package com.gmail.molnardad.quester.conditions;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.QElement;

@QElement("MONEY")
public final class MoneyCondition extends Condition {

	private final int amount;
	
	public MoneyCondition(int amount) {
		this.amount = amount;
	}

	@Override
	public boolean isMet(Player player) {
		if(!Quester.vault) {
			return true;
		}
		return Quester.econ.getBalance(player.getName()) >= amount;
	}
	
	@Override
	protected String parseDescription(String description) {
		return description.replaceAll("%amt", amount+"");
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
			desc = "requires player to have certain amount of money",
			usage = "<amount>")
	public static Condition fromCommand(QCommandContext context, CommandSender sender) throws QCommandException {
		try {
			int amt = context.getInt(1);
			return new MoneyCondition(amt);
		}
		catch (NumberFormatException e) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_GENERAL);
		}
	}
	
	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("amount", amount);
	}

	public static MoneyCondition deser(ConfigurationSection section) {
		int amt;
		
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		else
			return null;
		
		return new MoneyCondition(amt);
	}
}
