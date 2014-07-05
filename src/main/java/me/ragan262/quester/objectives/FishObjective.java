package me.ragan262.quester.objectives;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;

@QElement("FISH")
public final class FishObjective extends Objective {
	
	private final int amount;
	
	public FishObjective(final int amt) {
		amount = amt;
	}
	
	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(final int progress) {
		return "Catch fish - " + (amount - progress) + "x";
	}
	
	@Override
	protected String info() {
		return String.valueOf(amount);
	}
	
	@Command(min = 1, max = 1, usage = "<amount>")
	public static Objective fromCommand(final QuesterCommandContext context) throws CommandException {
		final int amt = context.getInt(0);
		if(amt < 1) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_POSITIVE"));
		}
		return new FishObjective(amt);
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
		return new FishObjective(amt);
	}
}
