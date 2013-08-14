package com.gmail.molnardad.quester.objectives;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.ExpManager;

@QElement("EXP")
public final class ExpObjective extends Objective {
	
	private final int amount;
	
	public ExpObjective(final int amt) {
		amount = amt;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(final int progress) {
		return "Have " + amount + " experience points on completion.";
	}
	
	@Override
	protected String info() {
		return String.valueOf(amount);
	}
	
	@Override
	public boolean tryToComplete(final Player player) {
		final int totalExp = new ExpManager(player).getCurrentExp();
		if(totalExp >= amount) {
			final ExpManager expMan = new ExpManager(player);
			expMan.changeExp(-amount);
			return true;
		}
		return false;
	}
	
	@QCommand(min = 1, max = 1, usage = "<amount>")
	public static Objective fromCommand(final QCommandContext context) throws QCommandException {
		final int amt = context.getInt(0);
		if(amt < 1) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_POSITIVE"));
		}
		return new ExpObjective(amt);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setInt("amount", amount);
	}
	
	protected static Objective load(final StorageKey key) {
		int amt = 0;
		amt = key.getInt("amount", 0);
		if(amt < 1) {
			return null;
		}
		return new ExpObjective(amt);
	}
	
	// Custom methods
	
	public int takeExp(final int amt) {
		return amt - amount;
	}
}
