package com.gmail.molnardad.quester.objectives;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;

@QElement("MILK")
public final class MilkObjective extends Objective {

	private final int amount;
	
	public MilkObjective(int amt) {
		amount = amt;
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(int progress) {
		return "Milk cow - " + (amount - progress) + "x";
	}
	
	@Override
	protected String info() {
		return String.valueOf(amount);
	}
	
	@QCommand(
			min = 1,
			max = 1,
			usage = "<amount>")
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
		int amt = context.getInt(0);
		if(amt < 1) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_POSITIVE);
		}
		return new MilkObjective(amt);
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("amount", amount);
	}
	
	public static Objective deser(ConfigurationSection section) {
		int amt = 0;
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		if(amt < 1)
			return null;
		return new MilkObjective(amt);
	}
}
