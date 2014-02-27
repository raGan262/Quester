package me.ragan262.quester.objectives;

import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;

import org.bukkit.entity.Player;

@QElement("MONEY")
public final class MoneyObjective extends Objective {
	
	private final double amount;
	
	public MoneyObjective(final double amt) {
		amount = amt;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(final int progress) {
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
	
	@Override
	public boolean tryToComplete(final Player player) {
		if(!Quester.vault) {
			return true;
		}
		final double money = Quester.econ.getBalance(player.getName());
		if(money >= amount) {
			if(Quester.vault) {
				Quester.econ.withdrawPlayer(player.getName(), amount);
			}
			return true;
		}
		return false;
	}
	
	@QCommand(min = 1, max = 1, usage = "<amount>")
	public static Objective fromCommand(final QCommandContext context) throws QCommandException {
		final double amt = context.getDouble(0);
		return new MoneyObjective(amt);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setDouble("amount", amount);
	}
	
	protected static Objective load(final StorageKey key) {
		double amt = 0;
		amt = key.getDouble("amount", 0);
		if(amt <= 0) {
			return null;
		}
		return new MoneyObjective(amt);
	}
	
	// Custom methods
	
	public double takeMoney(final double amt) {
		return amt - amount;
	}
}
