package com.gmail.molnardad.quester.objectives;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;

@QElement("MONEY")
public final class MoneyObjective extends Objective {

	private final double amount;
	
	public MoneyObjective(double amt) {
		amount = amt;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(int progress) {
		if(Quester.vault) {
			return "Get " + amount + " " + Quester.econ.currencyNamePlural();
		}
		else {
			return "Economy support disabled. (Completed)";
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
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
		double amt = context.getDouble(0);
		return new MoneyObjective(amt);
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("amount", amount);
	}
	
	public static Objective deser(ConfigurationSection section) {
		double amt = 0;
		if(section.isInt("amount") || section.isDouble("amount"))
			amt = section.getDouble("amount");
		if(amt <= 0)
			return null;
		return new MoneyObjective(amt);
	}

	@Override
	public boolean tryToComplete(Player player) {
		if(!Quester.vault) {
			return true;
		}
		double money = Quester.econ.getBalance(player.getName());
		if(money >= amount) {
			if(Quester.vault) {
				Quester.econ.withdrawPlayer(player.getName(), amount);
			}
			return true;
		}
		return false;
	}
	
	//Custom methods
	
	public double takeMoney(double amt) {
		return amt - amount;
	}
}
