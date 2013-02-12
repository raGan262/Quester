package com.gmail.molnardad.quester.objectives;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.utils.ExpManager;

@QElement("EXP")
public final class ExpObjective extends Objective {

	private final int amount;
	
	public ExpObjective(int amt) {
		amount = amt;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(int progress) {
		return "Have " + amount + " experience points on completion.";
	}
	
	@Override
	protected String info() {
		return String.valueOf(amount);
	}

	@Override
	public boolean tryToComplete(Player player) {
		int totalExp = new ExpManager(player).getCurrentExp();
		if(totalExp >= amount) {
			ExpManager expMan = new ExpManager(player);
			expMan.changeExp(-amount);
			return true;
		}
		return false;
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
		return new ExpObjective(amt);
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
		return new ExpObjective(amt);
	}
	
	// Custom methods
	
	public int takeExp(int amt) {
		return amt - amount;
	}
}
